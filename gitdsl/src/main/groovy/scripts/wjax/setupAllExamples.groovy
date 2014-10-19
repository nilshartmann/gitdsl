import gitdsl.Utils;
import scripts.wjax.WJaxUtils

final String BASE_PACKAGE = 'scripts.wjax';

def ALL_SCRIPTS = [
	// merges
	'merges.m10_fastforward',
	'merges.m20_fphistory',
	'merges.m25_squash',
	'merges.m30_mergeBase',
	'merges.m40_conflicts',
	// remotes
	'remotes.r01_remotes',
	'remotes.r02_refspecs',
	// gitflow
	'gitflow.g10_simple',
	// jgit
	'jgit.j10_simple',
	// build
	'build.b10_mavenRelPlugin'
];

Utils.deleteDirectory WJaxUtils.REMOTES_BASE_DIR
Utils.deleteDirectory WJaxUtils.REFSPECS_BASE_DIR
Utils.deleteDirectory WJaxUtils.MERGES_BASE_DIR
Utils.deleteDirectory WJaxUtils.GITFLOW_BASE_DIR
Utils.deleteDirectory WJaxUtils.JGIT_BASE_DIR
Utils.deleteDirectory WJaxUtils.BUILD_BASE_DIR

for (script in ALL_SCRIPTS) {

	String scriptName = "$BASE_PACKAGE.$script";

	println(" ======================= RUN $scriptName ===================== ");
	Script instance = Class.forName(scriptName).newInstance()
	instance.run();
}