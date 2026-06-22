param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$adb = "D:\Android\sdk\platform-tools\adb.exe"
$apk = Join-Path $projectRoot "app\build\outputs\apk\debug\app-debug.apk"

if (-not (Test-Path $adb)) {
    throw "ADB introuvable : $adb"
}

if (-not $SkipBuild) {
    $javaHome = "D:\Android\Android Studio\jbr"
    if (Test-Path $javaHome) {
        $env:JAVA_HOME = $javaHome
        $env:Path = "$javaHome\bin;$env:Path"
    }

    & (Join-Path $projectRoot "gradlew.bat") assembleDebug
    if ($LASTEXITCODE -ne 0) {
        throw "La compilation Android a échoué."
    }
}

if (-not (Test-Path $apk)) {
    throw "APK introuvable : $apk"
}

$devices = & $adb devices |
    Select-Object -Skip 1 |
    Where-Object { $_ -match "\sdevice$" } |
    ForEach-Object { ($_ -split "\s+")[0] }

$phones = @($devices | Where-Object { $_ -notmatch "^emulator-" })

if ($phones.Count -eq 0) {
    throw "Aucun téléphone détecté. Active le débogage USB, déverrouille le téléphone et accepte l'autorisation."
}

if ($phones.Count -gt 1) {
    throw "Plusieurs téléphones détectés : $($phones -join ', '). Débranche ceux qui ne doivent pas être ciblés."
}

$phone = $phones[0]
Write-Host "Installation sur $phone (progression conservée)..."
& $adb -s $phone install -r $apk
if ($LASTEXITCODE -ne 0) {
    throw "L'installation a échoué."
}

& $adb -s $phone shell monkey -p com.pokergrind.app -c android.intent.category.LAUNCHER 1 | Out-Null
Write-Host "PokerGrind est à jour et lancé sur le téléphone."
