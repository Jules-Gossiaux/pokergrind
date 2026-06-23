# Architecture

## 1. Objectifs

L'architecture doit rendre le MVP simple sans enfermer les futurs chapitres
dans un seul bloc. Le domaine poker et l'algorithme d'apprentissage ne doivent
pas dépendre de l'interface Android ou du stockage.

## 2. Structure actuelle

Le MVP reste dans un seul module Gradle `app`. Les frontières sont portées par
des packages :

```text
com.pokergrind.app
├── data
│   └── local
├── domain
│   ├── model
│   ├── statistics
│   └── training
└── ui
    ├── home
    ├── range
    ├── statistics
    ├── theme
    └── training
```

## 3. Structure cible à long terme

```text
app
├── navigation et assemblage de l'application
├── design-system
├── core
│   ├── model
│   ├── database
│   ├── data
│   └── testing
└── feature
    ├── home
    ├── training
    ├── range-viewer
    ├── progress
    └── settings
```

Au démarrage, ces frontières peuvent être des packages ou quelques modules
Gradle seulement. Un module séparé n'est créé que lorsqu'il apporte une vraie
isolation ; la modularité ne doit pas ralentir le MVP.

## 4. Couches

### Présentation

Écrans Compose, état d'interface et navigation. Les `ViewModel` transforment
les flux du domaine en états immuables et reçoivent les actions utilisateur.

### Domaine

Règles pures et testables :

- génération d'une question ;
- évaluation d'une réponse ;
- sélection des cartes de révision ;
- calcul de maîtrise ;
- progression, XP et série.

Cette couche ne connaît ni Compose, ni Room.

### Données

Repositories et sources locales :

- catalogue de ranges embarqué sous forme structurée ;
- base Room pour les réponses et révisions ;
- DataStore pour les préférences légères.

## 5. Flux d'une réponse

```text
Sélecteur de révision
        ↓
Question affichée
        ↓
Réponse de l'utilisateur
        ↓
Évaluation pure ───→ Feedback immédiat
        ↓
Historique + état de révision + progression
        ↓
Persistance locale
```

L'écriture locale est la source de vérité. L'état visible est dérivé de flux
observables, afin de survivre aux changements de configuration et aux
redémarrages.

## 6. Chapitres futurs

Un chapitre pédagogique expose à terme :

- son identifiant et ses métadonnées ;
- ses spots ordonnés ;
- les actions disponibles ;
- son générateur de questions ;
- ses règles de déblocage ;
- les composants d'explication spécifiques nécessaires.

Le moteur de révision, les sessions, l'XP et les statistiques restent communs.
Un nouveau chapitre ne doit pas modifier le fonctionnement interne des
chapitres existants.

## 7. Dépendances

Les dépendances vont vers le domaine :

```text
UI → cas d'usage / domaine ← repositories
                            ↑
                     stockage local
```

Le catalogue préflop peut être remplacé dans les tests par une version réduite.
L'horloge et le générateur aléatoire sont injectables pour rendre les tests
déterministes.

La navigation présente les grands chapitres dans `FoundationsScreen`.
Le chapitre `Opens` se déplie directement sur cette page pour afficher le
parcours BTN → SB, sans changer d'écran. Les futurs chapitres (`Défenses BB`,
`3-bets`) restent visibles dans la même liste et pourront adopter le même
modèle d'accordéon.

L'export local sérialise explicitement les entités Room et l'état DataStore
dans un fichier JSON versionné choisi via le sélecteur Android. La restauration
remplace les tables et l'état léger dans une opération contrôlée, sans copier
les fichiers internes pendant qu'ils sont ouverts.

## 8. Tests attendus

- tests exhaustifs des 169 catégories de mains par range ;
- tests des seuils de maîtrise et de leur réversibilité ;
- tests du tri des révisions dues ;
- tests de diversité et d'absence de répétitions accidentelles ;
- tests de persistance des sessions et de la série ;
- tests Compose des parcours critiques ;
- contrôle manuel de la matrice reconstruite face à la source.
