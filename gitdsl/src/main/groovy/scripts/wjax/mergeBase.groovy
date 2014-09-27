//
package scripts.wjax
import gitdsl.GitRepository

def commits(gitdsl.RepositoryScript gs, String branch) {
	gs.checkout branch;
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
	gs.modifyFile 'f1'; gs.commit();
}

// Neues Repository in /tmp/testrepo anlegen
// ACHTUNG! Das Verzeichnis wird GELÖSCHT, wenn es schon vorhanden ist
GitRepository.recreateAt("/Users/nils/develop/wjax2014_git_workshop/uebungen/merge-base/simple").setup {

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'master';

	// Feature Branch
	tag 'DAS_GESUCHTE_COMMIT'
	commits delegate, 'feature-1'

	// Noch mehr Commits auf dem Master
	commits delegate, 'master'
}

GitRepository.recreateAt("/Users/nils/develop/wjax2014_git_workshop/uebungen/merge-base/two-merges").setup {
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'master';

	// Feature Branch
	commits delegate, 'feature-1'

	// Noch mehr Commits auf dem Master
	commits delegate, 'master'
	tag 'DAS_GESUCHTE_COMMIT'

	// Mit Master aktualisieren
	checkout 'feature-1'
	merge 'master', message: 'Update from master'

	commits delegate, 'feature-1'

	commits delegate, 'master'

	// cd ../two-merges && git tag --points-at `git merge-base master feature-1`

}