package gitdsl

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository

import groovy.util.logging.Log;
import groovy.util.logging.Log4j2;
import groovy.util.logging.Slf4j;

@Log4j2
class RepositoryScript {

	static final def ln = System.getProperty('line.separator')

	final File repositoryRoot
	final Repository repository;
	final Map files = [:]
	int commitCount = 0;

	RepositoryScript(Repository repository) {
		assert repository;

		this.repository = repository;
		this.repositoryRoot = repository.getDirectory().getParentFile();
	}

	def addFile(Map args, String id=null) {

		if (!id) {
			id = "f${files.size()+1}"
		}

		// Sicherstellen, dass es noch keine Datei mit dieser Id gibt
		assert ! files[id]

		final String locationInRepo = args.get('path', '');
		final String content = args.get('content', "Line1${ln}Line2${ln}Line3");

		log.info "Create File '$locationInRepo' with id '$id'"

		RepositoryFile repositoryFile = new RepositoryFile(repositoryRoot, locationInRepo, content);
		files.put id, repositoryFile


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

		String locationInRepo = args.get('topath', '');
		String content = args.get('content');

		
		
		final oldLocationInRepo = repoFile.moveTo(locationInRepo, content)
		

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


	/**
	 * Aktiviert den angegebenen Branchnamen. Wenn der Branch noch nicht exisitert, wird er angelegt
	 * 
	 * <p>Mögliche Optionen (alle optional):
	 * <ul>
	 * <li>mergeCurrentBranch</li>: Mergt den aktuellen Branch nach dem auschecken des übergebenen Branches mit 'no-ff'
	 * </ul>
	 * @param branchName Der Name des Branches
	 * @return
	 */
	def checkout(Map args=new Hashtable(), String branchName) {
		final Git git = new Git(repository);
		final boolean orphan = args.get('orphan', false);
		final def branches = git.branchList().call()
		final def refName = "refs/heads/" + branchName;
		final boolean mergeCurrentBranch = args.get('mergeCurrentBranch');
		final String currentBranch = repository.getBranch();
		
		
		def createBranch = true;
		for (branch in branches) {
			if (refName == branch.getName()) {
				createBranch = false;
				break;
			}
		}
		
		log.info "${createBranch?'Erzeuge':'Aktiviere'} Branch '$branchName'. Orphan: $orphan"
		
		git.checkout().setCreateBranch(createBranch).setOrphan(orphan).setName(branchName).call();

	
		if (mergeCurrentBranch) {
			merge(currentBranch);
		}	
	}

	/**
	 * Mergt den angegebenen Branch in den aktuellen Branch. Per Default wird der Merge
	 * mit 'no-ff' ausgeführt, so dass ein Merge Commit entsteht.
	 * 
	 * <p>Mögliche Optionen (alle optinal)</p>
	 * <ul>
	 * <li>noff: true|false</li>
	 * <li>message: Commit-Message für den Commit-Kommentar
	 * </li>
	 * 
	 * @param branchName
	 * @return
	 */
	def merge(Map args=new Hashtable(), String branchName) {
		Git git = new Git(repository);
		String message = args.get('message');
		boolean noff = args.get('noff', true);

		ObjectId ref = repository.resolve("refs/heads/$branchName")

		log.info "Merge Branch '$branchName' (${ref.abbreviate(5).name()}) from '${repository.getFullBranch()}'. No-FastForward: $noff"
		
		MergeCommand merge = git.merge().include(ref).setCommit(!message)
		
		if (noff) {
			merge = merge.setFastForward(FastForwardMode.NO_FF)
		}

		merge.call()

		if (message) {
			git.commit().setMessage(message).call();
		}
	}
}
