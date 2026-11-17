package org.firstinspires.ftc.teamcode.helpers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class FileLoggerTest {
    private val logDir by lazy {
        listOf(File("/sdcard/logs"), File(System.getProperty("java.io.tmpdir"), "ftc_logs")).first { it.isDirectory }
    }
    private val dropWarning = Regex("""\[FileLogger] \[WARN] dropped (\d+) lines""")

    @Before
    fun reset() {
        FileLogger.info("reset", "consume any pending drop count")
        FileLogger.clearLogs()
    }

    private fun logFiles() = listOf("log.2.txt", "log.1.txt", "log.txt").map { File(logDir, it) }.filter { it.exists() }

    private fun readLines() = logFiles().flatMap { it.readLines() }

    private fun messages(lines: List<String>, tag: String) = lines.filter { "[$tag] " in it }.map { it.substringAfterLast("] ") }

    private fun droppedCount(lines: List<String>) = lines.sumOf { dropWarning.find(it)?.groupValues?.get(1)?.toInt() ?: 0 }

    private fun flushAndEmitDropCount() {
        FileLogger.flush()
        FileLogger.info("end", "end")
        FileLogger.flush()
    }

    @Test
    fun flushWritesEverythingLoggedBeforeItInOrder() {
        val n = 5_000
        repeat(n) { FileLogger.info("t", "$it") }
        FileLogger.flush()

        val lines = readLines()
        assertEquals((0 until n).map { "$it" }, messages(lines, "t"))
        assertEquals(0, droppedCount(lines))
    }

    @Test
    fun overloadDropsAreAllAccountedFor() {
        val n = 200_000
        repeat(n) { FileLogger.info("t", "$it") }
        flushAndEmitDropCount()

        val lines = readLines()
        val written = messages(lines, "t").map { it.toInt() }
        val dropped = droppedCount(lines)
        println("overload: written=${written.size} dropped=$dropped")
        assertTrue("expected the burst to overflow the queue", dropped > 0)
        assertEquals(n, written.size + dropped)
        assertEquals(written.sorted(), written)
    }

    @Test
    fun concurrentProducersLoseNothingUnaccounted() {
        val producers = 4
        val perProducer = 50_000
        val failure = AtomicReference<Throwable>()
        (0 until producers)
            .map { p ->
                thread {
                    try {
                        repeat(perProducer) { FileLogger.info("t", "$p:$it") }
                    } catch (e: Throwable) {
                        failure.set(e)
                    }
                }
            }.forEach { it.join() }
        flushAndEmitDropCount()

        failure.get()?.let { throw it }
        val lines = readLines()
        val written = messages(lines, "t")
        assertEquals(producers * perProducer, written.size + droppedCount(lines))
        for (p in 0 until producers) {
            val seq = written.filter { it.startsWith("$p:") }.map { it.substringAfter(':').toInt() }
            assertEquals("producer $p out of order", seq.sorted(), seq)
        }
    }

    @Test
    fun rotationKeepsThreeBoundedFiles() {
        val payload = "x".repeat(1_000)
        val lines = 20_000
        for (i in 0 until lines) {
            FileLogger.info("t", "$i $payload")
            if (i % 4_000 == 3_999) FileLogger.flush()
        }
        FileLogger.flush()

        val files = logFiles()
        assertEquals(3, files.size)
        files.forEach { assertTrue("${it.name} is ${it.length()} bytes", it.length() <= 5L * 1024 * 1024 + 2_000) }
        assertTrue(File(logDir, "log.txt").readLines().last().contains("${lines - 1} $payload"))
    }

    @Test
    fun clearLogsWhileLoggingDoesNotBreakTheWriter() {
        val failure = AtomicReference<Throwable>()
        val running = AtomicBoolean(true)
        val producer =
            thread {
                try {
                    var i = 0
                    while (running.get()) FileLogger.info("t", "${i++}")
                } catch (e: Throwable) {
                    failure.set(e)
                }
            }
        repeat(20) {
            FileLogger.clearLogs()
            Thread.sleep(5)
        }
        running.set(false)
        producer.join()

        failure.get()?.let { throw it }
        FileLogger.info("after", "still writing")
        FileLogger.flush()
        assertTrue(File(logDir, "log.txt").readLines().any { "[after] " in it })
    }

    @Test
    fun recreatesDeletedLogDir() {
        FileLogger.info("t", "before")
        FileLogger.clearLogs()
        assertTrue(logDir.deleteRecursively())

        FileLogger.info("t", "after")
        FileLogger.flush()
        assertEquals(listOf("after"), messages(readLines(), "t"))
    }

    @Test
    fun heapFloorStaysFlat() {
        val runtime = Runtime.getRuntime()
        fun settledUsed(): Long {
            FileLogger.flush()
            repeat(3) {
                System.gc()
                Thread.sleep(50)
            }
            return runtime.totalMemory() - runtime.freeMemory()
        }

        val floors =
            (0 until 10).map {
                repeat(20_000) { i -> FileLogger.info("t", "$i") }
                settledUsed()
            }
        val growthMb = (floors.last() - floors.first()) / (1024.0 * 1024.0)
        println("heap floors (MB): " + floors.joinToString { "%.2f".format(it / (1024.0 * 1024.0)) })
        assertTrue("post-GC heap floor grew %.2f MB".format(growthMb), growthMb < 2.0)
    }
}
