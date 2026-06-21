# ADR-0002 — Séparer les images sources des données livrées

- Statut : acceptée
- Date : 2026-06-21

## Contexte

Les ranges de référence sont fournies sous forme d'images issues d'un guide
pour débutant. L'application doit afficher une matrice native et interroger les
mains individuellement ; elle ne doit pas afficher les images brutes.

## Décision

- conserver les images de travail dans le dossier local `ranges/` ;
- ignorer ce dossier dans Git ;
- retranscrire chaque range dans un format structuré versionné ;
- stocker exactement une action principale pour chacune des 169 catégories ;
- valider chaque transcription par des tests et une comparaison visuelle ;
- documenter la provenance sans embarquer le document source.

## Conséquences

- l'APK ne dépend pas d'images difficiles à lire ou à adapter ;
- les décisions sont requêtables, testables et affichables dans un thème natif ;
- la transcription demande une revue humaine soigneuse ;
- toute nouvelle version d'une range doit être explicitement versionnée.

## Première source disponible

Une image locale indique une range d'ouverture BU/BTN de 45,40 %. La
transcription complète sera réalisée dans la tranche verticale Open BTN.
