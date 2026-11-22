package org.firstinspires.ftc.teamcode.helpers

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/** Rotating log in /sdcard/logs written by a daemon thread. log() never blocks: on a full queue the line is dropped and counted in a later WARN entry. */
object FileLogger {
    private const val MAX_FILE_SIZE = 5L * 1024 * 1024
    private const val NUM_FILES = 3

    enum class LogLevel { DEBUG, INFO, WARN, ERROR }

    private val dir =
        File("/sdcard/logs").takeIf { it.isDirectory || it.mkdirs() }
            ?: File(System.getProperty("java.io.tmpdir"), "ftc_logs").apply { mkdirs() }
    private val queue = ArrayBlockingQueue<Runnable>(8192)
    private val dropped = AtomicInteger()

    // Writer-thread only.
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US)
    private var out: BufferedWriter? = null
    private var size = 0L

    init {
        thread(isDaemon = true, name = "FileLogger") {
            while (true) {
                try {
                    queue.take().run()
                    if (queue.isEmpty()) out?.flush()
                } catch (e: Exception) {
                    close()
                }
            }
        }
    }

    fun log(level: LogLevel, tag: String, message: String) {
        val time = System.currentTimeMillis()
        if (!queue.offer { write(time, level, tag, message) }) dropped.incrementAndGet()
    }

    fun debug(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)

    fun info(tag: String, message: String) = log(LogLevel.INFO, tag, message)

    fun warn(tag: String, message: String) = log(LogLevel.WARN, tag, message)

    fun error(tag: String, message: String) = log(LogLevel.ERROR, tag, message)

    /** Blocks the caller (≤ ~2s) until lines logged before this call reach the OS; no fsync. */
    fun flush() = onWriter { out?.flush() }

    fun clearLogs() =
        onWriter {
            close()
            repeat(NUM_FILES) { file(it).delete() }
        }

    private fun onWriter(action: () -> Unit) {
        val done = CountDownLatch(1)
        try {
            if (queue.offer({ try { action() } finally { done.countDown() } }, 1, TimeUnit.SECONDS)) {
                done.await(1, TimeUnit.SECONDS)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun write(time: Long, level: LogLevel, tag: String, message: String) {
        val missed = dropped.getAndSet(0)
        if (missed > 0) append(format(time, LogLevel.WARN, "FileLogger", "dropped $missed lines"))
        append(format(time, level, tag, message))
    }

    private fun format(time: Long, level: LogLevel, tag: String, message: String) =
        "[${formatter.format(time)}] [$tag] [$level] $message\n"

    private fun append(line: String) {
        (out ?: openWriter()).write(line)
        size += line.length
        if (size > MAX_FILE_SIZE) rotate()
    }

    private fun openWriter(): BufferedWriter {
        dir.mkdirs()
        val file = file(0)
        size = file.length()
        return FileOutputStream(file, true).bufferedWriter().also { out = it }
    }

    private fun rotate() {
        close()
        for (i in NUM_FILES - 2 downTo 0) {
            file(i + 1).delete()
            file(i).renameTo(file(i + 1))
        }
    }

    private fun file(index: Int) = File(dir, if (index == 0) "log.txt" else "log.$index.txt")

    private fun close() {
        runCatching { out?.close() }
        out = null
    }
}
