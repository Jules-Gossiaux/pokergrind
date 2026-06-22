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

Depuis la racine du projet, la commande recommandée est :

```powershell
.\scripts\install-phone.ps1
```

Ce script :

1. reconstruit l'APK ;
2. ignore les émulateurs ;
3. sélectionne l'unique téléphone physique connecté ;
4. installe la mise à jour avec `-r` ;
5. lance PokerGrind.

La progression est conservée. Pour réinstaller un APK déjà compilé :

```powershell
.\scripts\install-phone.ps1 -SkipBuild
```

Commande ADB équivalente :

```powershell
D:\Android\sdk\platform-tools\adb.exe -s IDENTIFIANT_APPAREIL install -r D:\code\PokerGrind\app\build\outputs\apk\debug\app-debug.apk
```

L'option `-r` conserve les données locales et la progression. Sans `-s`, ADB
peut cibler le mauvais appareil si un émulateur est également démarré.

## Signature et conservation des données

Android accepte une mise à jour seulement si elle possède le même
`applicationId` et la même signature que l'application installée. Les APK de
debug sont signés automatiquement par Android Studio avec la clé debug de cet
ordinateur. Ils se mettent donc à jour sans perdre les données avec
`install -r` ou le bouton Run d'Android Studio.

Une APK release signée avec une clé privée durable sera nécessaire avant de
distribuer l'application ou de garantir les mises à jour depuis un autre
ordinateur. Elle n'est pas requise pour les tests personnels actuels.

## Lancer l'application

```powershell
D:\Android\sdk\platform-tools\adb.exe -s IDENTIFIANT_APPAREIL shell monkey -p com.pokergrind.app 1
```
