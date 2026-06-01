# Commandes Git pour modifier et envoyer sur une branche

## Envoyer vers Mahenitsoa-4173
```bash
git status -sb
git checkout Mahenitsoa-4173
git pull --rebase origin Mahenitsoa-4173
git add -A
git commit -m "votre message"
git push origin Mahenitsoa-4173
```

## Flux simple (branche deja creee)
```bash
git status -sb
git checkout NOM_DE_LA_BRANCHE
git pull --rebase origin NOM_DE_LA_BRANCHE
git add -A
git commit -m "votre message"
git push origin NOM_DE_LA_BRANCHE
```

## Si la branche n existe pas encore
```bash
git checkout -b NOM_DE_LA_BRANCHE
git push -u origin NOM_DE_LA_BRANCHE
```

## Si un changement local bloque le checkout (fichiers generes)
```bash
git stash push -u -m "temp: sauvegarde"
git checkout NOM_DE_LA_BRANCHE
git stash pop
```

## Rappel rapide
- Toujours verifier la branche active avant de commit.
- Toujours faire un pull --rebase avant de push.
- Eviter de versionner le dossier target/ (build).
