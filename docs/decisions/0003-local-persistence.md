# ADR-0003 — Séparer état léger et historique structuré

- Statut : acceptée
- Date : 2026-06-21

## Contexte

PokerGrind doit reprendre instantanément une session interrompue, mais aussi
conserver un historique requêtable pour calculer la maîtrise et, plus tard, la
répétition espacée.

## Décision

- utiliser DataStore pour l'état léger : session courante, XP et série ;
- utiliser Room pour les événements de réponse immuables ;
- identifier une réponse par `sessionId + questionIndex` ;
- exporter le schéma Room dans `app/schemas/` ;
- dériver la maîtrise depuis les 30 réponses les plus récentes.

Room 2.7.2 est retenu avec Kotlin 2.1.20. Room 2.8.4 a été écarté dans cette
configuration car son processeur de schéma entre en conflit avec la version de
sérialisation du compilateur déjà utilisée par le projet.

## Conséquences

- un double appui ne crée pas deux réponses ;
- les statistiques peuvent être recalculées depuis les événements ;
- les futures migrations de base disposent d'un schéma versionné ;
- une réponse et l'XP associé impliquent deux stockages locaux distincts, ce
  qui devra être surveillé si les règles de récompense deviennent plus
  complexes.
