//
package scripts.wjax.remotes
import static scripts.wjax.WJaxUtils.*;
import gitdsl.GitRepository
import gitdsl.Utils;


final def LOKALES_REPO_EINS="${REMOTES_BASE_DIR}/lokalesRepo"
final def REMOTE_REPO_EINS="${REMOTES_BASE_DIR}/remoteRepoEins.git"

final beispielRemotesTxt = Utils.recreateFile("$REMOTES_BASE_DIR/beispiel_01_remotes.txt");

beispielRemotesTxt << """
# Neues - leeres - Repository erzeugen

mkdir lokal
cd lokal

git init .


git remote add origin file://${REMOTE_REPO_EINS}

git ls-remote origin
git branch -vv

git fetch

git checkout integration


"""

final pushBeispiel = Utils.recreateFile("$REMOTES_BASE_DIR/beispiel_02_push.txt")
pushBeispiel << """
	
	# [Eins] Neuen Feature-Branch anlegen
	git checkout -b features/f4

	# Etwas aendern und committen
	echo "fsadfasd" >>readme.txt
	git commit -m "Readme fuer Feature 4 angepasst" readme.txt

	# Push => Fehlermeldung
	git push

	# Upstream-Branch über zwei Möglichkeiten
	git push --set-upstream origin features/f4

	# Noch ein Commit erzeugen
	echo "fsadfasdfsdf" >>readme.txt
	git commit -m "Noch ein Feature auf f4" readme.txt
	
	# geht jetzt:
	git push

	# push.default zeigen:
  	git config --get-regexp push.*

	# ->> 'simple'
	# was bedeutet das? 
	#   nur der aktive Branch wird gepusht, und nur solange der Name des remote-tracking-branches
    #   dem lokalen Namen des Branches entspricht.
	# BEISPIEL:
	# Feature-Branch 'f4' löschen und neu 'mappen'
	git branch -D features/f4
	git checkout -b f4 --track origin/features/f4
    
	# Funktioniert nicht
	git push

	# Moegliche Optionen: 

	# 1. push.default umsetzen
	git config push.default upstream
	
	# 2. push-refspec konfigurieren:
	git config remote.origin.push +refs/heads/*:refs/heads/features/*

	# 3. push HEAD:origin/features/f4

	# push => geht
	# git push
	# Problem: ALLE Branches werden jetzt nach refs/heads/FEATURES/ gepusht
	
	# Empfehlung:
	# 1. Lokalen Branch genauso benennen wie Remote-Tracking Branch und push.default simple verwenden
	# 2. Wenn lokaler Branch != Remote-Tracking-Branch explizit angeben git push origin ZIEL-BRANCH

"""

final refspecBeispielTxt = Utils.recreateFile("$REMOTES_BASE_DIR/beispiel_03_refspec.txt")
refspecBeispielTxt << """
[Eins]
git checkout features/feature-3

# Eine Aenderung vornehmen
echo "Aenderung F3" >> readme.txt
git commit -m "Aenderung F3" readme.txt

# Einen Tag machen
git tag BUILD_SUCCESS_FEATURE_3

# Auf review-Branch pushen
git push origin HEAD:refs/for/review/feature-3

# Tags pushen
git push BUILD_SUCCESS_FEATURE_3

[Zwei]
git fetch
  --> Nichts passiert

git fetch refs/for/review/*:refs/remotes/origin/my-reviews/*
 --> Tags und Branch wird abgeholt
# History Feature-3 anzeigen
git log ... 

[Eins]
# Vorherigen Commit löschen
git reset --hard HEAD^
# oder (Gerrit):
git commit --amend

git push -f feature-3:refs/for/review/feature-3

[Zwei]
git fetch refs/for/review/*:refs/remotes/origin/my-reviews/*

# non-fast-foward denied
git fetch +refs/for/review/*:refs/remotes/origin/my-reviews/*
# --> jetzt gehts


# Remote-Branch löschen
git push origin :refs/for/integrationtest/feature-3
# Tag löschen
git push origin :refs/tags/BUILD_FEATURE_3_SUCCESS

# remote-tracking-branch löschen
git fetch --prune
"""


final pushForceBeispiel = Utils.recreateFile("$REMOTES_BASE_DIR/beispiel_04_push-force.txt")
pushForceBeispiel << """
	

### BEISPIEL --force-with-lease
	# Usecase: Lokales Umschreiben der History erzwingt force,
	#  es soll aber sichergestellt werden, dass sich die History
	#  des REmote-Branches nicht verändert hat in der Zwischenzeit
		git push --force-with-lease

#	 ! [rejected]        features/f2 -> features/f2 (stale info)
"""




Utils.deleteDirectory("$REMOTES_BASE_DIR/eins");
Utils.deleteDirectory("$REMOTES_BASE_DIR/zwei");

final cloneZweiMal = Utils.recreateFile("$REMOTES_BASE_DIR/clone-zweimal.sh");
cloneZweiMal.setExecutable(true);
cloneZweiMal << """#!/bin/bash

cd $REMOTES_BASE_DIR

  rm -rf eins
  rm -rf zwei

  git clone file:///${REMOTE_REPO_EINS} eins
  git clone file:///${REMOTE_REPO_EINS} zwei

"""

/*
 def commits(gitdsl.RepositoryScript gs, String branch) {
 gs.checkout branch, startPoint: 'master';
 gs.modifyFile 'f1'; gs.commit();
 gs.modifyFile 'f1'; gs.commit();
 gs.modifyFile 'f1'; gs.commit();
 }
 */

GitRepository.recreateAt("${REMOTE_REPO_EINS}.tmp").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'integration'
	tag 'BUILD_1'
	commits delegate, 'features/f1'
	commits delegate, 'features/f2'
	commits delegate, 'features/f3'

	checkout 'master', create: false
}.createAsBareAt("${REMOTE_REPO_EINS}", deleteSource: true)


//GitRepository.recreateAt("${BASE_DIR}/remoteRepoCi").setup {
//	// Initialer Commit
//	addFile 'f1', path: 'version.txt'
//	commit 'Initial Import remoteRepoCi'
//
//	commits delegate, 'build'
//}



