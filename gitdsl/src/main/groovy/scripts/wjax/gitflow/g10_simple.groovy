package scripts.wjax.gitflow

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=GITFLOW_BASE_DIR;

final def LOKALES_REPO="${BASE_DIR}/10_simple"

Utils.recreateFile("$BASE_DIR/beispiel_10_ff-noff.txt") << """
	cd ${LOKALES_REPO}

"""

GitRepository.recreateAt("${LOKALES_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	addFile 'f2', path: 'version.txt', content: 'HEAD'
	commit 'Initial Import'

	checkout 'develop'


	commits delegate, 'feature/implementRestApi', startPoint: 'develop', mergeTo: 'develop'
	commits delegate, 'feature/improveSecurity', startPoint: 'develop', mergeTo: 'develop'
	commits delegate, 'feature/replaceMavenWithGradle', startPoint: 'develop', mergeTo: 'develop'

	checkout 'release/v0.1'

	modifyFile 'f2', content: 'Version 0.1'; commit "Release 0.1 fertig"

	checkout 'master', create:false
	merge 'release/v0.1'
	tag 'v0.1'
	checkout 'develop'
	merge 'release/v0.1'

	// Release 0.2
	commits delegate, 'feature/upgradeSpringVersion', startPoint: 'develop', mergeTo: 'develop'
	commits delegate, 'feature/inspectPerformance', startPoint: 'develop', mergeTo: 'develop'

	checkout 'release/v0.2'
	modifyFile 'f2', content: 'Version 0.2'; commit "Release 0.2 fertig"
	checkout 'master', create:false
	merge 'release/v0.2'
	tag 'v0.2'
	checkout 'develop'
	merge 'release/v0.2'
}
