# Modèle de données

Ce document décrit le modèle conceptuel. Les noms Kotlin et le schéma Room
pourront évoluer, mais les responsabilités doivent rester stables.

## 1. Concepts poker

### HandCategory

Une des 169 catégories préflop canoniques :

- paire : `AA`, `77` ;
- main assortie : `A8s`, `76s` ;
- main dépareillée : `AKo`, `J8o`.

La notation stockée est normalisée avec la carte la plus haute en premier.

### Action

Actions actuellement implémentées par le domaine :

- `OPEN`
- `FOLD`

Actions prévues pour les prochains modules :

- `CALL`
- `THREE_BET`
- `FOUR_BET`

Le premier module n'utilise que `OPEN` et `FOLD`.

### Spot

Un spot définit le contexte d'une décision :

| Champ | Exemple |
|---|---|
| id | `open_btn_100bb` |
| chapterId | `preflop_foundations` |
| order | `1` |
| heroPosition | `BTN` |
| stackDepthBb | `100` |
| previousAction | aucune |
| sizingBb | `2.5` |
| availableActions | `OPEN`, `FOLD` |

### RangeDefinition

Associe chaque catégorie de main d'un spot à une unique action principale.
Une définition valide contient exactement 169 entrées, sans doublon ni trou.

La version est incluse dans l'identifiant, par exemple
`open_btn_100bb_v1`. La provenance, la date de transcription, le statut de
validation et les contrôles de frontière sont documentés dans `docs/ranges/`
et verrouillés par les tests.

Les images originales ne font pas partie du modèle livré.

## 2. Apprentissage

### ReviewItem

État de répétition pour le couple `spot + handCategory` :

- date de prochaine révision ;
- intervalle courant ;
- facteur de stabilité ou niveau d'apprentissage ;
- nombre de réussites consécutives ;
- nombre d'erreurs ;
- date de dernière présentation ;
- indicateur de réapparition dans la session.

### AnswerRecord

Événement immuable représentant une réponse :

- identifiant ;
- session et mode ;
- spot et catégorie de main ;
- action attendue et action choisie ;
- résultat ;
- durée de réponse ;
- date et heure.

L'historique permet de recalculer les statistiques et la maîtrise sans dépendre
d'un compteur fragile.

Dans l'implémentation Room, la clé primaire combine l'identifiant de session et
l'index de la question. Une même question ne peut donc pas être enregistrée
deux fois, même après un double appui ou une reprise.

Chaque réponse possède également un mode `GUIDED` ou `FREE`. Les requêtes de
maîtrise filtrent explicitement le mode guidé.

### TrainingSession

L'état persistant actuel contient :

- identifiant de session ;
- mode `GUIDED` ou `FREE` ;
- liste ordonnée des 20 questions ;
- index courant ;
- nombre de bonnes réponses ;
- action sélectionnée sur la question courante.

Une série quotidienne n'est mise à jour qu'à la fin d'une session quotidienne
complète. DataStore conserve deux emplacements indépendants, un pour la session
guidée et un pour la session libre. Les deux modes peuvent donc être suspendus
et repris sans s'écraser.

### SpotProgress

Vue dérivée contenant :

- état verrouillé, disponible ou maîtrisé ;
- taux de réussite sur les 30 dernières réponses ;
- couverture récente par action ;
- diversité récente des mains ;
- temps moyen récent ;
- date de dernière pratique.

La maîtrise n'est pas stockée comme une vérité permanente : elle est recalculée
depuis les résultats récents.

## 3. Progression globale

Le profil local contient actuellement :

- total d'XP ;
- série courante ;
- date de dernière session quotidienne terminée.

Le barème attribue 10 XP par bonne réponse. Le niveau dérivé de l'XP et la
meilleure série restent à définir.

## 4. Persistance du premier incrément

Le premier incrément utilise DataStore pour conserver :

- l'ordre des 20 mains de chaque mode ;
- l'index courant de chaque mode ;
- le score de chaque mode ;
- l'action déjà choisie sur la question courante de chaque mode ;
- le total d'XP ;
- la série et la date de dernière session terminée.

Room est désormais utilisé pour l'historique immuable des réponses :

- spot et catégorie de main ;
- action attendue et choisie ;
- résultat ;
- temps de réponse ;
- horodatage ;
- session et index de question.

Room contient aussi :

- `review_items`, un état de répétition par couple spot-main ;
- `spot_unlocks`, la liste permanente des spots déjà débloqués.

Les statistiques sont calculées par des requêtes d'agrégation Room sur
l'historique. Elles ne sont pas stockées comme des compteurs séparés, ce qui
évite qu'elles divergent des réponses réelles.

DataStore reste responsable du petit état transactionnel de l'interface :
session en cours, XP et série.

## 5. Validation des ranges

Pour chaque fichier de données :

1. vérifier les 169 catégories canoniques ;
2. vérifier qu'une seule action est assignée à chacune ;
3. comparer le pourcentage de combos avec la source lorsque celui-ci est fourni ;
4. générer la matrice dans l'app ;
5. effectuer une revue visuelle ;
6. verrouiller le résultat par des tests.
