# Downloads the whisper.cpp CPU binaries and the base.en model needed for Phase 2 (voice input).
# Run once after cloning the repo: powershell -ExecutionPolicy Bypass -File scripts/setup-voice-engine.ps1

$root = Resolve-Path "$PSScriptRoot\.."
$whisperDir = Join-Path $root "voice-engine\whisper"
$modelsDir = Join-Path $root "voice-engine\models"

New-Item -ItemType Directory -Force -Path $whisperDir | Out-Null
New-Item -ItemType Directory -Force -Path $modelsDir | Out-Null

$zipPath = Join-Path $whisperDir "whisper-bin-x64.zip"
if (-not (Test-Path (Join-Path $whisperDir "bin\Release\whisper-cli.exe"))) {
    Write-Host "Downloading whisper.cpp Windows x64 CPU binaries..."
    Invoke-WebRequest -Uri "https://github.com/ggml-org/whisper.cpp/releases/download/b5454/whisper-bin-x64.zip" -OutFile $zipPath
    Expand-Archive -Path $zipPath -DestinationPath (Join-Path $whisperDir "bin") -Force
    Remove-Item $zipPath
} else {
    Write-Host "whisper-cli.exe already present, skipping download."
}

$modelPath = Join-Path $modelsDir "ggml-base.en.bin"
if (-not (Test-Path $modelPath)) {
    Write-Host "Downloading whisper base.en model (~148 MB)..."
    Invoke-WebRequest -Uri "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.en.bin" -OutFile $modelPath
} else {
    Write-Host "Model already present, skipping download."
}

Write-Host "Voice engine setup complete."
