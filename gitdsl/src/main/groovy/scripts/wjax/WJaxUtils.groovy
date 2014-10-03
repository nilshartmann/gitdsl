package scripts.wjax

import groovy.util.logging.Log4j2;

@Log4j2

class WJaxUtils {

	private final static boolean ENABLE_DELETE = false;

	private final static String BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/uebungen';

	final static String baseDir(String name, boolean deleteDir) {
		assert name;

		final String theDir = BASE_DIR + "/" + name;

		if (deleteDir && ENABLE_DELETE) {
			log.info "WARNING! Delete directory $theDir"

			File directory = new File(theDir);
			if (directory.isDirectory()) {
				directory.deleteDir();
			}
		}

		return theDir;
	}

	public final static String MERGES_DIR = BASE_DIR + "/merges";
}
