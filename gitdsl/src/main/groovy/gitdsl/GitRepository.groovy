package gitdsl

import java.io.File;

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
	}
	
		
	
}
