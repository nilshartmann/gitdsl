package gitdsl

class Utils {

	static File recreateFile(String path) {
		assert path;

		File file = new File(path);

		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				assert file.getParentFile().mkdirs()
			}
		} else {
			assert file.delete()
		}

		assert file.createNewFile();

		return file
	}
}
