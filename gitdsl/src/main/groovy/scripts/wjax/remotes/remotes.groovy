//
package scripts.wjax.remotes
import scripts.wjax.WJaxUtils;
import gitdsl.GitRepository
import gitdsl.Utils;

final def BASE_DIR=WJaxUtils.baseDir 'remotes', true;


final def LOKALES_REPO_EINS="${BASE_DIR}/lokalesRepo"
final def REMOTE_REPO_EINS="${BASE_DIR}/remoteRepoEins.git"

final beispielTxt = Utils.recreateFile("$BASE_DIR/beispiele.txt");

beispielTxt << """
Lokales Repository erzeugen
  cd $BASE_DIR
  git clone file:///${REMOTE_REPO_EINS} eins
  cd eins

 # Branches und Tags anzeigen
  git branch -a
  git tag -l

  # Commit erzeugen
  echo "Eine Aenderung" >>readme.txt && git commit -m "Eine Aenderung" readme.txt

  # Remote-Status anzeigen 1 ahead
  git branch -vv

	# Ein Tag erzeugen
	git tag UEBUNG_1

	git push
	# ==> Push-Ausgabe analysieren: keine Tags :-(
	git ls-remote origin
	# ==> ebenfalls kein neuer Tag

    # Tag pushen
	git push origin UEBUNG_1


	# In ZWEITES Repository wechseln
	cd cd $BASE_DIR/zwei

	# Pullen
	git pull

	# => Kein neuer Tag
	git ls-remote
	
	git fetch --tags
	# oder:
	git fetch origin refs/tags/*:refs/tags/*


##### ZWEITER TEIL: PUSH-SPEC
	# Neuen Feature-Branch anlegen
	git checkout -b features/f4

	# Etwas aendern und committen
	echo "fsadfasd" >>readme.txt
	git commit -m "Readme fuer Feature 4 angepasst" readme.txt

	# Push => Fehlermeldung
	git push

	# Upstream-Branch über zwei Möglichkeiten
	
	

	# push.default zeigen:
  	git config --get-regexp push.*

	# ->> 'simple'
	# was bedeutet das? 
	#   nur der aktive Branch wird 
	


"""


Utils.deleteDirectory("$BASE_DIR/eins");
Utils.deleteDirectory("$BASE_DIR/zwei");

final cloneZweiMal = Utils.recreateFile("$BASE_DIR/clone-zweimal.sh");
cloneZweiMal.setExecutable(true);
cloneZweiMal << """#!/bin/bash

cd $BASE_DIR

  rm -r eins
  rm -r zwei

  git clone file:///${REMOTE_REPO_EINS} eins
  git clone file:///${REMOTE_REPO_EINS} zwei

"""

def commits(gitdsl.RepositoryScript gs, String branch) {
	gs.checkout branch, startPoint: 'master';
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
}

GitRepository.recreateAt("${REMOTE_REPO_EINS}.tmp").setup {
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



