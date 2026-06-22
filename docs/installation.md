# Installation sur un téléphone Android

## Prérequis

- activer les options développeur et le débogage USB ;
- utiliser un câble prenant en charge les données ;
- déverrouiller le téléphone et accepter l'autorisation de débogage ;
- générer l'APK avec `./gradlew assembleDebug`.

## Vérifier l'appareil

```powershell
D:\Android\sdk\platform-tools\adb.exe devices
```

Un téléphone physique doit apparaître avec l'état `device`. `emulator-5554`
désigne l'émulateur et non le téléphone.

## Installer ou mettre à jour

```powershell
D:\Android\sdk\platform-tools\adb.exe -s IDENTIFIANT_APPAREIL install -r D:\code\PokerGrind\app\build\outputs\apk\debug\app-debug.apk
```

L'option `-r` conserve les données locales et la progression. Sans `-s`, ADB
peut cibler le mauvais appareil si un émulateur est également démarré.

## Lancer l'application

```powershell
D:\Android\sdk\platform-tools\adb.exe -s IDENTIFIANT_APPAREIL shell monkey -p com.pokergrind.app 1
```
