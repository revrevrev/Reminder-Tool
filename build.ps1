<#
.SYNOPSIS
    Build the ReminderApp Android project.

.PARAMETER Task
    Gradle task to run. Default: assembleDebug
    Other useful values: assembleRelease, clean, tasks

.PARAMETER Clean
    Run 'clean' before building.

.EXAMPLE
    .\build.ps1
    .\build.ps1 -Task assembleRelease
    .\build.ps1 -Clean
#>
param(
    [string]$Task  = "assembleDebug",
    [switch]$Clean
)

$ErrorActionPreference = "Stop"   # applies to PS cmdlets only; native exes use $LASTEXITCODE
$ProjectRoot = $PSScriptRoot

function Write-Step { param($msg) Write-Host "" ; Write-Host ">> $msg" -ForegroundColor Cyan }
function Write-Ok   { param($msg) Write-Host "   OK  $msg" -ForegroundColor Green }
function Write-Warn { param($msg) Write-Host "   !!  $msg" -ForegroundColor Yellow }
function Write-Fail { param($msg) Write-Host "   XX  $msg" -ForegroundColor Red }

# --- 1. Java ------------------------------------------------------------------
Write-Step "Checking Java"

$javaExe = $null
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
} else {
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($javaCmd) { $javaExe = $javaCmd.Source }
}

if (-not $javaExe) {
    Write-Fail "Java not found."
    Write-Warn "Set JAVA_HOME to your JDK 17+ directory."
    Write-Warn "Android Studio's JBR is at: C:\Program Files\Android\Android Studio\jbr"
    exit 1
}

Write-Ok "Java: $javaExe"

if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = Split-Path (Split-Path $javaExe)
    Write-Warn "JAVA_HOME was not set; using $env:JAVA_HOME"
}

# --- 2. Android SDK -----------------------------------------------------------
Write-Step "Checking Android SDK"

$sdkRoot = $env:ANDROID_HOME
if (-not $sdkRoot) { $sdkRoot = $env:ANDROID_SDK_ROOT }
if (-not $sdkRoot) {
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "$env:USERPROFILE\AppData\Local\Android\Sdk",
        "C:\Android\Sdk"
    )
    $sdkRoot = $candidates | Where-Object { Test-Path $_ } | Select-Object -First 1
}

if (-not $sdkRoot) {
    Write-Fail "Android SDK not found."
    Write-Warn "Install Android Studio or set ANDROID_HOME to your SDK path."
    exit 1
}
Write-Ok "SDK: $sdkRoot"

$localProps = Join-Path $ProjectRoot "local.properties"
$sdkPath    = $sdkRoot -replace "\\", "/"
Set-Content -Path $localProps -Value "sdk.dir=$sdkPath" -Encoding UTF8
Write-Ok "Wrote local.properties"

# --- 3. Gradle wrapper jar ----------------------------------------------------
Write-Step "Checking Gradle wrapper"

$wrapperDir = Join-Path $ProjectRoot "gradle\wrapper"
$wrapperJar = Join-Path $wrapperDir "gradle-wrapper.jar"

if (-not (Test-Path $wrapperJar)) {
    Write-Warn "gradle-wrapper.jar missing - downloading from GitHub..."
    $jarUrl = "https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar"
    $downloaded = $false
    try {
        $wc = New-Object System.Net.WebClient
        $wc.DownloadFile($jarUrl, $wrapperJar)
        $downloaded = $true
        Write-Ok "Downloaded gradle-wrapper.jar"
    } catch {
        Write-Warn "Download failed: $_"
    }

    if (-not $downloaded) {
        $gradleCmd = Get-Command gradle -ErrorAction SilentlyContinue
        if ($gradleCmd) {
            Write-Warn "Bootstrapping from system Gradle..."
            Push-Location $ProjectRoot
            & $gradleCmd.Source wrapper --gradle-version 8.6
            Pop-Location
        }
    }

    if (-not (Test-Path $wrapperJar)) {
        Write-Fail "Cannot obtain gradle-wrapper.jar."
        Write-Host ""
        Write-Host "Fix options:" -ForegroundColor Yellow
        Write-Host "  A) Allow internet access and re-run."
        Write-Host "  B) Install Gradle 8+ (https://gradle.org/install) then re-run."
        Write-Host "  C) Open in Android Studio - it manages the wrapper automatically."
        exit 1
    }
    Write-Ok "Wrapper ready"
} else {
    Write-Ok "gradle-wrapper.jar present"
}

# --- 4. Build -----------------------------------------------------------------
$gradlew = Join-Path $ProjectRoot "gradlew.bat"

if ($Clean) {
    Write-Step "Running: clean"
    cmd /c "`"$gradlew`" clean"
    if ($LASTEXITCODE -ne 0) { Write-Fail "Clean failed (exit $LASTEXITCODE)"; exit $LASTEXITCODE }
    Write-Ok "Clean done"
}

Write-Step "Running: $Task"
cmd /c "`"$gradlew`" $Task"
$exitCode = $LASTEXITCODE

# --- 5. Result ----------------------------------------------------------------
Write-Host ""
if ($exitCode -eq 0) {
    Write-Ok "Build succeeded!"
    $apkDir = Join-Path $ProjectRoot "app\build\outputs\apk"
    $apks   = Get-ChildItem -Path $apkDir -Filter "*.apk" -Recurse -ErrorAction SilentlyContinue |
              Sort-Object LastWriteTime -Descending
    if ($apks) {
        Write-Host ""
        Write-Host "Output APK(s):" -ForegroundColor Cyan
        foreach ($apk in $apks) {
            $size = "{0:N1} MB" -f ($apk.Length / 1MB)
            Write-Host "  $($apk.FullName)  ($size)"
        }
    }
} else {
    Write-Fail "Build FAILED (exit $exitCode)"
    Write-Host ""
    Write-Host "Common fixes:" -ForegroundColor Yellow
    Write-Host "  * Fonts: add TTF files to app/src/main/res/font/ and un-comment Font() in Type.kt"
    Write-Host "  * Sounds: replace placeholder .ogg files in app/src/main/res/raw/"
    Write-Host "  * SDK: verify ANDROID_HOME or open the project in Android Studio"
    exit $exitCode
}
