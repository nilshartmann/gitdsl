package gitdsl

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository

import groovy.util.logging.Log;

@Log
class RepositoryScript {
	
	static final def ln = System.getProperty('line.separator')
	
	File location
	Repository repository;
	Map files = [:]
	int commitCount = 0;

	RepositoryScript(Repository repository) {
		assert repository;
		
		this.repository = repository;
		this.location = repository.getDirectory().getParentFile();
		
	}
		
	def addFile(Map args, String id=null) {

		if (!id) {
			id = "f${files.size()+1}"
		}
		
		assert ! files[id]
		
		String locationInRepo = args.get('path', '');
		String content = args.get('content', "Line1${ln}Line2${ln}Line3");
		
		if (locationInRepo.startsWith('/')) {
			locationInRepo = locationInRepo.substring(1);
		}
		
		log.info "Create File '$locationInRepo' with id '$id'"
		
		File newFile = new File(location, locationInRepo);
		newFile.getParentFile().mkdirs();
		newFile.createNewFile();
		
		RepositoryFile repositoryFile = new RepositoryFile(newFile, locationInRepo);
		files.put id, repositoryFile
		
		repositoryFile.writeContent content;
		
		return repositoryFile
	}
	
	def commit(String message) {
		commitCount++;
		
		if (!message) {
			message = "Commit No $commitCount";
		}
		
		Git git = new Git(repository);
		git.add().addFilepattern(".").call();
		git.commit().setMessage(message).call();
	}
	
	
	
}
