$ErrorActionPreference = "Stop"

# Ensure TLS 1.2
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# Configuration
$JdkUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.10_7.zip"
$InstallDir = Join-Path $PSScriptRoot ".tools"
$JdkZip = Join-Path $InstallDir "temp_jdk17.zip"
$JdkTargetDir = Join-Path $InstallDir "jdk-17"

# Check if JDK 17 is already installed locally
# We look for java.exe in the expected extraction path. 
# Note: The zip usually contains a root folder like 'jdk-17.0.10+7'.
# We will check dynamically after extraction, but for idempotency:

if (Test-Path "$JdkTargetDir\bin\java.exe") {
    Write-Host "[OK] JDK 17 found locally at $JdkTargetDir" -ForegroundColor Green
    exit 0
}

# If we have a subfolder from previous extraction (handling the nested folder issue)
$ExistingJdk = Get-ChildItem -Path $InstallDir -Filter "jdk-17*" -Directory | Where-Object { Test-Path "$($_.FullName)\bin\java.exe" } | Select-Object -First 1
if ($ExistingJdk) {
    Write-Host "[OK] JDK 17 found locally at $($ExistingJdk.FullName)" -ForegroundColor Green
    # Rename it to standard name if needed, or just use it.
    # For simplicity, we just exit 0 and let the batch script find it via the same logic or we fix the path here.
    # Actually, for the batch script to be simple, we really want it at .tools/jdk-17.
    if ($ExistingJdk.Name -ne "jdk-17") {
        Move-Item $ExistingJdk.FullName $JdkTargetDir -Force
        Write-Host "[INFO] Renamed to $JdkTargetDir" -ForegroundColor Cyan
    }
    exit 0
}

Write-Host "[INFO] Downloading OpenJDK 17 (LTS)..." -ForegroundColor Yellow
if (-not (Test-Path $InstallDir)) { New-Item -ItemType Directory -Force -Path $InstallDir | Out-Null }

Invoke-WebRequest -Uri $JdkUrl -OutFile $JdkZip
Write-Host "[INFO] Extracting JDK..." -ForegroundColor Cyan
Expand-Archive -Path $JdkZip -DestinationPath $InstallDir -Force

# Cleanup Zip
Remove-Item $JdkZip -Force

# Flatten Directory Structure: The zip extracts to 'jdk-17.0.10+7', verify and rename to 'jdk-17'
$ExtractedFolder = Get-ChildItem -Path $InstallDir -Filter "jdk-17*" -Directory | Where-Object { $_.Name -ne "jdk-17" } | Select-Object -First 1

if ($ExtractedFolder) {
    Rename-Item $ExtractedFolder.FullName "jdk-17"
    Write-Host "[OK] JDK 17 installed to $JdkTargetDir" -ForegroundColor Green
}
elseif (Test-Path "$JdkTargetDir\bin\java.exe") {
    Write-Host "[OK] JDK 17 installed correctly." -ForegroundColor Green
}
else {
    Write-Host "[ERROR] Could not locate extracted JDK bin folder." -ForegroundColor Red
    exit 1
}
