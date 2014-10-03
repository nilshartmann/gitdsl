//
package scripts.wjax.merges
import java.util.Map;

import scripts.wjax.WJaxUtils;
import gitdsl.GitRepository
import gitdsl.Utils;


final Map branchCommitMap = new Hashtable<String, Integer>();

def commits(Map args = new Hashtable(), gitdsl.RepositoryScript gs, String branch) {
	gs.checkout branch;
	final int commits = args.get('commits', 3);

	for (int i=1;i<(commits+1);i++) {
		int commitCount = gs.counter.next("commits_on_branch_$branch")
		String line = "$branch: $commitCount. Commit"
		gs.modifyFile 'f1', add: line; gs.commit line;
	}


}

final def BASE_DIR=WJaxUtils.baseDir 'merges/merge-base', true;



final beispielTxt = Utils.recreateFile("$BASE_DIR/beispiel_01.txt");
beispielTxt << """
  cd $BASE_DIR/merge-base
  
	# history zeigen

   git merge-base HEAD master
	
   git tag --points-at \$(git merge-base HEAD master)

	# oder:
    # git show-ref --tags

   # Was ist auf dem Topic-Branch veraendet seit der Merge Base?
   git diff \$(git merge-base HEAD master)

"""

GitRepository.recreateAt("$BASE_DIR/merge-base").setup {

	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt', content: "readme.txt, initial Version\n\n"
	commit 'Initial Import'

	commits delegate, 'master', commits: 2;
	tag 'ERWARTETE_MERGE_BASE', annotated: false

	// Feature Branch
	commits delegate, 'feature-1'


	// Noch mehr Commits auf dem Master
	commits delegate, 'master', commits: 4


	// Commits auf feature-1
	commits delegate, 'feature-1'
}

GitRepository.recreateAt("$BASE_DIR/merge-base-complex").setup {

	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt', content: "readme.txt, initial Version\n\n"
	commit 'Initial Import'

	commits delegate, 'master', commits: 2;


	// Feature Branch
	commits delegate, 'feature-1'

	// Noch mehr Commits auf dem Master
	commits delegate, 'master', commits: 4
	tag 'ERWARTETE_MERGE_BASE', annotated: false

	checkout 'feature-1', create:false
	merge 'master', strategy:'ours'

	// Noch mehr Commits auf dem Master
	commits delegate, 'master', commits: 4


	// Commits auf feature-1
	commits delegate, 'feature-1'
}
