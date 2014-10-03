package gitdsl

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult
import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class Utils {

	static void deleteDirectory(String path) {
		assert path;

		File directory = new File(path);
		if (directory.isDirectory()) {
			assert directory.deleteDir();
		}
	}

	static File recreateFile(String path) {
		assert path;

		File file = new File(path);

		return recreateFile(file);
	}

	static File recreateFile(File file) {

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

	// copyDirectory(repository.getDirectory().getAbsolutePath(), targetPath);

	static void copyDirectory(String from, String targetPath) {
		FileSystem fs = FileSystems.getDefault();

		Path tp = fs.getPath(targetPath);
		Path sourcePath =  fs.getPath(from);


		Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(final Path dir,
							final BasicFileAttributes attrs) throws IOException {
						Files.createDirectories(tp.resolve(sourcePath
								.relativize(dir)));
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(final Path file,
							final BasicFileAttributes attrs) throws IOException {
						Files.copy(file,
								tp.resolve(sourcePath.relativize(file)));
						return FileVisitResult.CONTINUE;
					}
				});
	}
}
