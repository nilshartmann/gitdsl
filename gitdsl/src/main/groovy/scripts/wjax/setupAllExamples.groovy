import gitdsl.Utils;
import scripts.wjax.WJaxUtils

final String BASE_PACKAGE = 'scripts.wjax';

def ALL_SCRIPTS = [
	// merges
	'merges.m01_fastforward',
	'merges.m02_fphistory',
	'merges.m03_mergeBase',
	'merges.m04_conflicts',
	// remotes
	'remotes.r01_remotes',
	'remotes.r02_refspecs'
];

Utils.deleteDirectory WJaxUtils.REMOTES_BASE_DIR
Utils.deleteDirectory WJaxUtils.REFSPECS_BASE_DIR
Utils.deleteDirectory WJaxUtils.MERGES_BASE_DIR


for (script in ALL_SCRIPTS) {

	String scriptName = "$BASE_PACKAGE.$script";

	println(" ======================= RUN $scriptName ===================== ");
	Script instance = Class.forName(scriptName).newInstance()
	instance.run();
}