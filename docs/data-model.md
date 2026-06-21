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

Actions prévues par le domaine :

- `OPEN`
- `FOLD`
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

Chaque range possède aussi :

- une version ;
- une description de provenance ;
- une date de transcription ;
- un statut de validation ;
- une empreinte permettant de détecter une modification involontaire.

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

### TrainingSession

- type : quotidienne ou libre ;
- date de début et de fin ;
- cible de questions ;
- nombre de réponses ;
- statut : en cours, terminée ou abandonnée.

Une série quotidienne n'est mise à jour qu'à la fin d'une session quotidienne
complète.

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

Le profil local contient :

- total d'XP ;
- niveau dérivé de l'XP ;
- série courante et meilleure série ;
- date de dernière session quotidienne terminée.

Le barème d'XP et la courbe de niveaux seront documentés avant codage de cette
partie.

## 4. Validation des ranges

Pour chaque fichier de données :

1. vérifier les 169 catégories canoniques ;
2. vérifier qu'une seule action est assignée à chacune ;
3. comparer le pourcentage de combos avec la source lorsque celui-ci est fourni ;
4. générer la matrice dans l'app ;
5. effectuer une revue visuelle ;
6. verrouiller le résultat par des tests.
