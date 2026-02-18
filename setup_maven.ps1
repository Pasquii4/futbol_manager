param (
    [string]$JavaHomeParam = ""
)

$ErrorActionPreference = "Stop"

# Ensure TLS 1.2 is used
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# 1. Setup Java Environment (Optional)
if (-not [string]::IsNullOrWhiteSpace($JavaHomeParam)) {
    if (Test-Path "$JavaHomeParam\bin\java.exe") {
        Write-Host "[INFO] Setting JAVA_HOME from argument: $JavaHomeParam" -ForegroundColor Cyan
        $env:JAVA_HOME = $JavaHomeParam
        $env:PATH = "$JavaHomeParam\bin;$env:PATH"
    }
}

# 2. Check Java Availability (Soft Check)
try {
    $javaOutput = java -version 2>&1 | Out-String
    Write-Host "[OK] Java found." -ForegroundColor Green
}
catch {
    Write-Host "[WARNING] Java verification failed. Continuing anyway to setup Maven..." -ForegroundColor Yellow
}

# Define Maven version and URL
$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$installDir = Join-Path $PSScriptRoot ".tools"
$mavenHome = Join-Path $installDir "apache-maven-$mavenVersion"
$mavenBin = Join-Path $mavenHome "bin"

# 3. Check / Install Maven
if (Test-Path "$mavenBin\mvn.cmd") {
    Write-Host "[OK] Maven found locally in $mavenBin" -ForegroundColor Green
    exit 0
}

# Check system maven
if (Get-Command "mvn" -ErrorAction SilentlyContinue) {
    Write-Host "[OK] Maven is already installed in PATH." -ForegroundColor Green
    exit 0
}

Write-Host "[INFO] Maven not found. Downloading Apache Maven $mavenVersion..." -ForegroundColor Yellow
if (-not (Test-Path $installDir)) {
    New-Item -ItemType Directory -Force -Path $installDir | Out-Null
}

$zipPath = Join-Path $installDir "maven.zip"

try {
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipPath
    Write-Host "[INFO] Extracting Maven..." -ForegroundColor Cyan
    Expand-Archive -Path $zipPath -DestinationPath $installDir -Force
    
    if (Test-Path "$mavenBin\mvn.cmd") {
        Write-Host "[OK] Maven installed successfully." -ForegroundColor Green
    }
    else {
        throw "Extraction failed."
    }
}
catch {
    Write-Host "[ERROR] Failed to download or install Maven: $_" -ForegroundColor Red
    exit 1
}
finally {
    if (Test-Path $zipPath) { Remove-Item $zipPath -Force }
}
