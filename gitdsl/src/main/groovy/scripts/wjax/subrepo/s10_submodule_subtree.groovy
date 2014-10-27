package scripts.wjax.subrepo

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

Utils.deleteDirectory SUBREPO_BASE_DIR


GitRepository.recreateAt("${SUBREPO_BASE_DIR}/submodule/web-application").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'
	copyExternalDirectory '/Users/nils/develop/wjax2014_git_workshop/git-repos/maventest/gitdsl/wjax-repositories/submodule_subtree/web-application'
	commit "Initial Import"
}.createAsBareAt("${SUBREPO_BASE_DIR}/submodule/web-application.git", deleteSource: true)

//GitRepository.recreateAt("${SUBREPO_BASE_DIR}/subtree/web-application").setup {
//	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'
//	copyExternalDirectory '/Users/nils/develop/wjax2014_git_workshop/git-repos/maventest/gitdsl/wjax-repositories/submodule_subtree/web-application'
//	commit "Initial Import"
//} //.createAsBareAt("${SUBREPO_BASE_DIR}/subtree/web-application.git", deleteSource: true)
//


Utils.copyDirectory(
		'/Users/nils/develop/wjax2014_git_workshop/git-repos/maventest/gitdsl/wjax-repositories/submodule_subtree/bootstrap',
		"${SUBREPO_BASE_DIR}/submodule/bootstrap");

GitRepository.useExisting("${SUBREPO_BASE_DIR}/submodule/bootstrap").setup {
	addFile 'theme.css', path: 'dist/css/bootstrap-theme.css'

	modifyFile 'theme.css', content: '''
/**
 * Bootstrap Mini Theme w-jax Demo
 *
 * v4.0
 */

.page-header h1 {
        color: rgb(240,80,51);
}
.page-header:after {
        font-size:12px;
        content: ' [Bootstrap v4.0]'
}
'''
	commit "Set color for Page title"
	tag 'v4.0'

	checkout 'develop', startPoint: 'master'

	modifyFile 'theme.css', content: '''
/**
 * Bootstrap Mini Theme w-jax Demo
 *
 * v4.1
 */

.page-header h1 {
        color: rgb(240,80,51);
		text-transform: uppercase;
		border-bottom: 4px solid rgb(240,80,51);
}
.page-header:after {
        font-size:12px;
        content: ' [Bootstrap v4.1]'
}
'''
	commit "Add border to title"
	tag 'v4.1'

	checkout 'master', create:false


}.createAsBareAt("${SUBREPO_BASE_DIR}/submodule/bootstrap.git", deleteSource: true)

Utils.copyDirectory "${SUBREPO_BASE_DIR}/submodule", "${SUBREPO_BASE_DIR}/subtree"


//Utils.copyDirectory(
//		'/Users/nils/develop/wjax2014_git_workshop/git-repos/maventest/gitdsl/wjax-repositories/submodule_subtree/bootstrap.git',
//		"${SUBREPO_BASE_DIR}/subtree/bootstrap.git");


