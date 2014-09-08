package gitdsl

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
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
		RepositoryFile repositoryFile = new RepositoryFile(newFile, locationInRepo);
		files.put id, repositoryFile

		repositoryFile.writeContent content;

		return repositoryFile
	}

	def modifyFile(Map args, String id) {
		RepositoryFile repoFile = files[id];
		assert repoFile;

		repoFile.writeContent(args.content);
	}

	def moveFile(Map args, String id) {
		RepositoryFile repoFile = files[id];
		assert repoFile;

		final oldLocationInRepo = repoFile.locationInRepository;

		String locationInRepo = args.get('topath', '');
		String content = args.get('content', "Line1${ln}Line2${ln}Line3");

		if (locationInRepo.startsWith('/')) {
			locationInRepo = locationInRepo.substring(1);
		}

		File newFile = new File(location, locationInRepo);

		repoFile.moveTo newFile, locationInRepo
		if (content) {
			repoFile.writeContent content
		}

		Git git = new Git(repository)
		git.rm().addFilepattern(oldLocationInRepo).call();
	}
	
	def removeFile(String id) {
		RepositoryFile repoFile = files.remove(id);
		assert repoFile;
		
		Git git = new Git(repository)
		git.rm().addFilepattern(repoFile.locationInRepository).call();
	}

	def commit(String message) {
		commitCount++;

		if (!message) {
			message = "Commit No $commitCount";
		}

		Git git = new Git(repository);
		git.add().addFilepattern('.').call();
		git.commit().setMessage(message).call();
	}


	def checkout(Map args=new Hashtable(), String branchName) {
		Git git = new Git(repository);
		def orphan = args.get('orphan', false);
		println "branch: $branchName, orphan: $orphan"
		def branches = git.branchList().call()
		def createBranch = true;
		def refName = "refs/heads/" + branchName;
		for (branch in branches) {
			if (refName == branch.getName()) {
				createBranch = false;
				break;
			}
		}
		git.checkout().setCreateBranch(createBranch).setOrphan(orphan).setName(branchName).call();
	}

	def merge(Map args=new Hashtable(), String branchName) {
		Git git = new Git(repository);
		String message = args.get('message');
		boolean noff = args.get('noff', false);

		ObjectId ref = repository.resolve("refs/heads/$branchName")

		println("Noff: $noff");

		MergeCommand merge = git.merge().include(ref).setCommit(!message)


		if (noff) {
			merge = merge.setFastForward(FastForwardMode.NO_FF)
		}

		merge.call()

		if (message) {
			println("Commit mit Message $message");
			git.commit().setMessage(message).call();
		}
	}
}
