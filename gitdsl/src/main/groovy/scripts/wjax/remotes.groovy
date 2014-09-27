//
package scripts.wjax
import gitdsl.GitRepository

final def BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/uebungen/remotes';

final def LOKALES_REPO="${BASE_DIR}/lokalesRepo"
final def REMOTE_REPO_EINS="${BASE_DIR}/remoteRepoEins.git"

final beispielTxt = new File("$BASE_DIR/beispiele.txt");
if (beispielTxt.exists()) {
	beispielTxt.delete();
}

beispielTxt << """
SZENARIO 1: Klonen und Remote untersuchen


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

  # git config --get-regexp push.*


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
	tag 'BUILD_2'
	commits delegate, 'features/f2'
	tag 'BUILD_3'
	commits delegate, 'features/f3'
	tag 'BUILD_4'

	checkout 'master', create: false
}.createAsBareAt("${REMOTE_REPO_EINS}", deleteSource: true)


//GitRepository.recreateAt("${BASE_DIR}/remoteRepoCi").setup {
//	// Initialer Commit
//	addFile 'f1', path: 'version.txt'
//	commit 'Initial Import remoteRepoCi'
//
//	commits delegate, 'build'
//}



