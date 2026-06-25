# Feuille de route

Cette feuille de route décrit l'ordre de travail, sans date de livraison.

## Réalisé — Socle, ranges d'open et défenses BB

- application Android Kotlin/Compose et thème sombre ;
- cinq ranges d'open 6-max : BTN, CO, HJ, UTG et SB ;
- cinq spots de défense BB : BTN, CO, SB, HJ et UTG avec `Call`, `3-bet` et `Fold` ;
- validation automatique des 169 catégories et des pourcentages sources ;
- parcours progressif avec déblocages permanents ;
- sessions guidées multi-spots de 20 questions ;
- entraînement libre ciblé sur un spot débloqué ;
- répétition espacée par couple spot-main ;
- reprise des sessions guidées, XP et série quotidienne ;
- reprise indépendante et simultanée des sessions guidée, libre globale et
  libre ciblée ;
- stockage Room et DataStore avec migrations versionnées ;
- table 6-max native et matrices de ranges reconstruites ;
- consultation d'une range depuis l'accueil ou après une réponse ;
- statistiques par spot, classements et matrice colorée par main ;
- accueil Fondations avec chapitre Opens déroulant, prêt à accueillir les prochains chapitres ;
- export et restauration locale de toute la progression ;
- tests unitaires, Android Lint et essais sur émulateur et téléphone.

## Prochaine étape — Stabilisation du MVP

1. recueillir et corriger les retours d'usage quotidien sur téléphone ;
2. enrichir le résumé de fin de session : spots travaillés, erreurs et
   révisions restantes ;
3. ajouter des tests Compose sur les parcours critiques ;
4. vérifier l'accessibilité et les tailles d'écran ;
5. ajouter des tests automatisés d'import/export.

La signature release est volontairement reportée : pendant le développement,
l'APK debug reste plus simple à construire, installer et mettre à jour.

## Après les défenses de BB

1. 3-bet SB vs BTN ;
2. 3-bet BB vs BTN ;
3. défense contre 3-bet ;
4. récapitulatif préflop ;
5. autres fondations postflop.
