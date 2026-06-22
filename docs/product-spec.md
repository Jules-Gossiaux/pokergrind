# Spécification produit — MVP

## 1. Objectif

PokerGrind aide l'utilisateur à automatiser ses décisions préflop. L'app ne
cherche pas seulement à montrer une bonne réponse : elle organise les
révisions pour faire revenir rapidement les erreurs et espacer les acquis.

La V1 est en français, fonctionne hors ligne et cible Android sous forme
d'APK.

## 2. Environnement poker

| Élément | Décision |
|---|---|
| Format | Cash game 6-max |
| Profondeur | 100 BB |
| Open raise | 2,5 BB |
| 3-bet en position | 8 BB, pour une étape future |
| 3-bet hors position | 9 BB, pour une étape future |
| 4-bet | Sizing à définir plus tard |
| Mixes | Une seule action principale par main |
| Langue | Français |

Les positions affichées en 6-max sont UTG, HJ, CO, BTN, SB et BB. L'interface
emploie `UTG` plutôt que `LJ / UTG` pour éviter une double nomenclature.

## 3. Premier module livrable

Le premier incrément couvre uniquement les open ranges, dans cet ordre :

1. Open BTN
2. Open CO
3. Open HJ
4. Open UTG
5. Open SB

Un spot verrouillé devient disponible lorsque le spot précédent est maîtrisé.

Les cinq ranges d'open sont intégrées. Les sessions guidées
garantissent qu'au moins quatre questions proviennent de chaque range
débloquée et disponible, avant de consacrer le reste du budget aux révisions
les plus prioritaires.

## 4. Parcours préflop ultérieur

Après les open ranges :

1. Défense BB vs BTN
2. Défense BB vs CO
3. Défense BB vs SB
4. 3-bet SB vs BTN
5. 3-bet BB vs BTN
6. Défense contre les 3-bets
7. Récapitulatif général

Cette liste sert à préserver la cohérence de l'architecture. Elle n'autorise
pas son implémentation dans le premier incrément.

## 5. Question d'entraînement

Une question présente :

- le spot et la position ;
- la profondeur de 100 BB ;
- le sizing d'open de 2,5 BB ;
- une catégorie de main, par exemple `A8s` ;
- les actions possibles, `Open` ou `Fold` pour les open ranges ;
- un chronomètre discret, sans pénalité.

Après la réponse :

- `Correct` ou `Incorrect` est affiché immédiatement ;
- la bonne action est indiquée ;
- le bouton `Voir la range` ouvre une matrice reconstruite par l'application ;
- l'utilisateur peut rapidement passer à la question suivante.

Chaque range débloquée peut également être consultée depuis sa carte sur
l'accueil, sans lancer une session.

## 6. Modes

### Session quotidienne

- environ 20 questions ;
- priorité aux révisions dues et aux erreurs ;
- les révisions excédentaires sont reportées ;
- une erreur importante peut revenir dans la même session ;
- terminer la session valide l'activité du jour pour la série.

Dans l'interface, ce mode porte le nom **Session guidée**, car plusieurs blocs
de 20 questions peuvent être réalisés dans une même journée. Seul le premier
bloc guidé terminé entretient la série du jour.

### Entraînement libre

- blocs de 20 questions répétables ;
- choix parmi les spots débloqués dont la range est disponible ;
- une bonne réponse rapporte de l'XP et alimente les statistiques libres ;
- une bonne réponse ne compte pas dans les 30 réponses de maîtrise ;
- une erreur rend la main prioritaire dans les futures sessions guidées ;
- il ne remplace pas la session guidée pour la série.
- quitter une session libre l'abandonne ;
- démarrer une session guidée remplace une éventuelle session libre en cours.

## 7. Maîtrise d'un spot

Un spot est maîtrisé lorsque toutes les conditions suivantes sont réunies :

- au moins 30 réponses enregistrées dans la fenêtre courante ;
- au moins 90 % de bonnes réponses sur les 30 dernières réponses ;
- couverture minimale des catégories d'actions ;
- diversité suffisante des catégories de mains.

La maîtrise est réversible. Si les performances récentes tombent sous le seuil,
le spot redevient à travailler. Le temps de réponse est mesuré mais n'entre pas
dans la validation du MVP.

Les nombres exacts requis pour la couverture par action seront calibrés avec
les données réelles des ranges. Ils ne doivent pas rendre impossible la
maîtrise d'une range très déséquilibrée.

Dans le parcours, l'objectif principal est formulé simplement comme
`27 bonnes réponses sur les 30 dernières`. La condition de diversité reste
contrôlée par le moteur, sans alourdir l'écran principal.

Pour le premier calibrage, la diversité exige au moins :

- 8 catégories de mains distinctes dont l'action attendue est Open ;
- 8 catégories de mains distinctes dont l'action attendue est Fold.

Lorsque toutes les conditions sont remplies, le spot suivant est débloqué. La
maîtrise est recalculée après chaque réponse et peut être perdue, sans
reverrouiller les spots déjà atteints.

## 8. Progression et statistiques

La V1 comprend :

- XP ;
- taux de réussite global et par spot ;
- temps de réponse moyen, à titre informatif ;
- série quotidienne ;
- progression et état de maîtrise de chaque spot.

Règle initiale d'XP : une bonne réponse rapporte de l'XP ; aucune mécanique
d'achat, de classement, de badge complexe ou de sanction n'est prévue.

Barème du premier incrément :

- une bonne réponse rapporte 10 XP ;
- une erreur ne retire pas d'XP ;
- la série est validée une seule fois par jour, à la fin des 20 questions ;
- une session guidée interrompue reprend à la question exacte, avec son score
  et son feedback conservés ;
- une session libre est volontairement abandonnée lorsque l'utilisateur la
  quitte.

L'écran Statistiques affiche une matrice par spot. Le remplissage représente le
taux de réussite historique de la main et la couleur indique son niveau :
vert à partir de 90 %, orange entre 80 et 89 %, rouge sous 80 %, gris sans
réponse guidée.

## 9. Hors périmètre

- backend, compte et synchronisation ;
- paiement, publicité et classement ;
- tournoi, Expresso et ICM ;
- solveur et fréquences GTO ;
- sizings variables ;
- postflop ;
- internationalisation de l'interface.

## 10. Critères de réussite du MVP

- une session de 20 questions peut être terminée confortablement à une main ;
- aucune connexion réseau n'est nécessaire ;
- les erreurs sont reproposées plus tôt que les acquis ;
- les ranges affichées correspondent exactement aux données validées ;
- fermer et rouvrir l'app conserve progression, historique et session ;
- l'interface reste lisible et réactive sur un téléphone Android courant.
