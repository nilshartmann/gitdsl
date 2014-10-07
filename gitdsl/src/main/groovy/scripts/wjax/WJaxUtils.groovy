package scripts.wjax

import java.util.Map;

import groovy.util.logging.Log4j2;

@Log4j2
class WJaxUtils {

	private final static boolean ENABLE_DELETE = false;

	private final static String BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/beispiele';

	final static String baseDir(String name) {
		assert name;

		final String theDir = BASE_DIR + "/" + name;

		//		if (deleteDir && ENABLE_DELETE) {
		//			log.info "WARNING! Delete directory $theDir"
		//
		//			File directory = new File(theDir);
		//			if (directory.isDirectory()) {
		//				directory.deleteDir();
		//			}
		//		}

		return theDir;
	}

	final static def commits(Map args = new Hashtable(), gitdsl.RepositoryScript gs, String branch) {
		gs.checkout branch, startPoint: 'master';

		final int commits = args.get('commits', 3);

		for (int i = 0; i<commits;i++) {
			int commitCount = gs.counter.next("commits_on_branch_$branch")
			String line = "$branch: $commitCount. Commit"

			if (args.content) {
				gs.modifyFile 'f1', content: args.content; gs.commit line;
			} else if (args.add) {
				gs.modifyFile 'f1', add: args.add; gs.commit line;
			} else {
				gs.modifyFile 'f1', add: line; gs.commit line;
			}
		}


		String mergeTo = args.get("mergeTo");
		if (mergeTo) {
			gs.checkout mergeTo
			gs.merge branch, message: "Finished $branch: '$args.subject' (Merged into $mergeTo)"
		}
	}

	final static def commitsStartsAtMaster(Map args = new Hashtable(), gitdsl.RepositoryScript gs, String branch) {
		args.put('startPoint', 'master');
		WJaxUtils.commits(args, gs, branch);
	}

	final static String MERGES_BASE_DIR = baseDir("merges");
	final static String REMOTES_BASE_DIR = baseDir("remotes");
	final static String REFSPECS_BASE_DIR = baseDir("refspecs");
}