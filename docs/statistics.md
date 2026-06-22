# Statistiques

## Objectif

L'écran Statistiques rend visible le niveau actuel sans confondre pratique et
certification.

## Données affichées

- réponses guidées et taux de réussite historique par spot ;
- progression actuelle sur les 30 dernières réponses ;
- temps de réponse moyen ;
- mains les plus faibles et les plus fortes ;
- matrice 13 × 13 par spot, dont chaque case est remplie selon le taux de
  réussite historique de la main ;
- volume séparé d'entraînement libre.

Une case grise indique qu'aucune réponse guidée n'existe encore. Le remplissage
représente directement le pourcentage de réussite historique de la main. Sa
couleur facilite la lecture :

- vert : 90 à 100 % ;
- orange : 80 à 89 % ;
- rouge : moins de 80 % ;
- gris : aucune réponse guidée.

La légende utilise des pastilles colorées compactes. La couleur complète le
pourcentage et ne remplace pas l'information chiffrée.

Une main doit avoir au moins deux réponses guidées avant d'entrer dans les
classements forts/faibles. Ce seuil volontairement bas sera relevé lorsque
l'historique utilisateur sera plus important.

## Interprétation

- la maîtrise est calculée sur une fenêtre glissante récente ;
- le taux historique donne une vue plus longue ;
- une bonne réponse libre ne modifie pas la certification ;
- une erreur libre influence le planificateur guidé.
