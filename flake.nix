{
  description = "FTC BIOBUZZ 2026-2027 robot controller";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";

      pkgs = import nixpkgs {
        inherit system;
        config = {
          allowUnfree = true;
          android_sdk.accept_license = true;
        };
      };

      # Matches compileSdk in build.common.gradle.
      android = pkgs.androidenv.composeAndroidPackages {
        platformVersions = [ "37.0" ];
        buildToolsVersions = [ "37.0.0" "36.0.0" ];
        includeEmulator = false;
        includeSystemImages = false;
        includeSources = false;
      };

      sdk = "${android.androidsdk}/libexec/android-sdk";
      jdk = pkgs.jdk21;
    in {
      devShells.${system}.default = pkgs.mkShell {
        packages = [ jdk pkgs.android-tools pkgs.kotlin-language-server ];

        JAVA_HOME = jdk.home;
        ANDROID_HOME = sdk;
        ANDROID_SDK_ROOT = sdk;

        GRADLE_OPTS = builtins.concatStringsSep " " [
          "-Dorg.gradle.project.android.aapt2FromMavenOverride=${sdk}/build-tools/37.0.0/aapt2"
          "-Dorg.gradle.project.android.builder.sdkDownload=false"
        ];
      };
    };
}
