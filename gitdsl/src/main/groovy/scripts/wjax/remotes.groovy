//
package scripts.wjax
import gitdsl.GitRepository

final def BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/uebungen/remotes';

new File("$BASE_DIR/beispiele.txt") << """
SZENARIO 1: Bestehendes Repository in leeres Remote-Repository übertragen



"""

def commits(gitdsl.RepositoryScript gs, String branch) {
	gs.checkout branch, startPoint: 'master';
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
}

def lokalesRepo = GitRepository.recreateAt("${BASE_DIR}/lokalesRepo").setup {
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'integration'
	commits delegate, 'features/f1'
	commits delegate, 'features/f2'
	commits delegate, 'features/f3'

	checkout 'master', create: false
}.createAsBareAt("${BASE_DIR}/remoteRepoEins.git")
.createAsBareAt("${BASE_DIR}/remoteRepoZwei.git")


//GitRepository.recreateAt("${BASE_DIR}/remoteRepoCi").setup {
//	// Initialer Commit
//	addFile 'f1', path: 'version.txt'
//	commit 'Initial Import remoteRepoCi'
//
//	commits delegate, 'build'
//}



