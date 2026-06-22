# Changelog

Tous les changements notables du projet sont consignés dans ce fichier.

Le format suit les principes de
[Keep a Changelog](https://keepachangelog.com/fr/1.1.0/). Le projet n'a pas
encore de version publiée.

## [Non publié]

### Ajouté

- cadrage fonctionnel du MVP ;
- architecture Android cible ;
- modèle de données conceptuel ;
- stratégie initiale de répétition espacée ;
- direction visuelle et feuille de route ;
- politique de conservation des images de ranges.
- squelette Android Kotlin et Jetpack Compose ;
- thème sombre PokerGrind ;
- transcription structurée de la range Open BTN à 45,40 % ;
- session locale de 20 questions Open/Fold ;
- feedback immédiat et matrice de range native ;
- tests unitaires des 169 mains et des 602 combinaisons ouvertes.
- persistance DataStore de la session en cours, de l'XP et de la série ;
- reprise d'une session à la question exacte après navigation ou redémarrage ;
- attribution de 10 XP par bonne réponse ;
- mise à jour de la série à la fin d'une session quotidienne complète ;
- chemin visuel Fondations avec les cinq spots d'open ;
- amélioration du contraste, de la palette et de la hiérarchie visuelle ;
- action explicite `Open 2,5 BB` dans l'entraînement.
- critère de déblocage du prochain spot affiché dans le parcours ;
- connecteurs du parcours conservés neutres tant que le spot suivant est verrouillé.
- historique structuré de chaque réponse avec Room ;
- progression BTN calculée sur une fenêtre glissante de 30 réponses ;
- validation de la maîtrise à 27/30 avec diversité minimale Open/Fold ;
- déblocage automatique et réversible d'Open CO ;
- protection contre le double enregistrement d'une même réponse ;
- schéma Room versionné pour préparer les futures migrations.
- modes distincts Session guidée et Entraînement libre ;
- sélecteur de position pour le mode libre ;
- certification alimentée uniquement par les réponses guidées ;
- erreurs libres transformées en priorités pour le guidé ;
- répétition d'une erreur guidée environ trois questions plus tard ;
- répétition espacée par couple spot-main avec intervalles 1, 3, 7, 14 jours et plus ;
- planificateur guidé compatible avec plusieurs ranges débloquées ;
- déblocages de spots conservés définitivement ;
- migration Room 1 vers 2 sans perte de progression.
- transcription et validation de la range Open CO à 22,78 % ;
- maîtrise indépendante pour BTN et CO ;
- sessions guidées garanties multi-spots avec un minimum par range débloquée ;
- sélection BTN ou CO dans l'entraînement libre ;
- premier écran Statistiques par spot et par main ;
- classement des points forts et mains à travailler avec seuil de fiabilité ;
- déblocage permanent de HJ après maîtrise de CO.
- transcription et validation des ranges Open HJ, UTG et SB ;
- parcours complet des cinq positions d'open, avec déblocages successifs ;
- représentation native d'une table 6-max pendant les questions ;
- matrice statistique 13 × 13 remplie selon la réussite de chaque main ;
- sélection de la position dans les statistiques et le mode libre ;
- actions d'accueil compactées pour préserver l'espace du parcours.
- abandon explicite d'une session libre depuis l'écran d'entraînement ;
- passage direct du mode libre à une nouvelle session guidée ;
- défilement du sélecteur de spots avec bouton Retour toujours accessible ;
- boutons Voir la range et Continuer alignés à taille identique.
- consultation des ranges débloquées directement depuis le parcours d'accueil ;
- matrice de maîtrise colorée en vert, orange ou rouge selon le taux par main ;
- documentation produit, technique, installation et feuille de route
  réalignées sur la version 0.7.0.
- légende de maîtrise remplacée par des pastilles colorées compactes ;
- script PowerShell de compilation, mise à jour et lancement sur téléphone
  physique sans perte de progression.
- sessions guidée et libre persistées indépendamment et reprises à leur
  question exacte sans s'écraser ;
- migration transparente des anciennes sessions DataStore vers la lecture
  multi-mode ;
- priorité des révisions dues placée avant les mains nouvelles ;
- augmentation progressive de la priorité après plusieurs erreurs sur une même
  main ;
- audit et documentation de l'algorithme de répétition espacée, avec sa
  différence explicite par rapport à Anki/FSRS.
- détail minimal et replié des prochaines révisions dans Statistiques ;
- nombre de révisions dues visible sans exposer les paramètres techniques du
  moteur.
