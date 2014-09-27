package gitdsl

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult
import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository

import groovy.util.logging.Log;
import groovy.util.logging.Log4j2;

@Log4j2
class GitRepository {

	Repository repository;

	//	def static at(String path) {
	//		assert path;
	//		def repositoryDir = new File(path)
	//		assert repositoryDir.isDirectory(), "Repository directory at '$path' does not exists or is not a folder";
	//
	//		new GitRepository(location: repositoryDir);
	//	}



	def static recreateAt(String path) {
		assert path

		def repositoryDir = new File(path)

		log.info "Recreate Repository Root $repositoryDir"

		if (repositoryDir.isDirectory()) {
			repositoryDir.deleteDir()
		} else if (repositoryDir.isFile()) {
			repositoryDir.delete()
		}

		repositoryDir.mkdirs();

		File gitDir = new File(repositoryDir, ".git");

		Repository repository = new FileRepository(gitDir);
		repository.create();

		new GitRepository(repository: repository);
	}

	def setup(closure) {
		closure.delegate = new RepositoryScript(repository);
		closure();

		return this;
	}

	/**
	 * Kopiert dieses Repository in ein neues Bare-Repository an der angegebenen Stelle
	 */
	def createAsBareAt(Map args = new Hashtable(), String targetPath) {
		log.info("Create a bare repository to $targetPath");

		File directory = new File(targetPath);

		if (directory.isDirectory()) {
			directory.deleteDir()
		} else if (directory.isFile()) {
			directory.delete()
		}

		FileSystem fs = FileSystems.getDefault();

		Path tp = fs.getPath(targetPath);
		Path sourcePath =  fs.getPath(repository.getDirectory().getAbsolutePath());


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


		// https://git.wiki.kernel.org/index.php/GitFaq#How_do_I_make_existing_non-bare_repository_bare.3F
		Repository newRepository = new FileRepository(directory);
		newRepository.getConfig().setBoolean("core", null, "bare", true);
		newRepository.getConfig().save();
		newRepository.close();

		if (args.deleteSource) {
			File sourceRepoDir = repository.directory.parentFile;
			log.info "Delete source repository from ${sourceRepoDir}"
			repository.close();
			sourceRepoDir.deleteDir();
		}

		return this;
	}



}
