package gitdsl

import groovy.util.logging.Log4j2

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.MergeCommand.FastForwardMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.merge.MergeStrategy

@Log4j2
class RepositoryScript {

	static final def ln = System.getProperty('line.separator')

	final File repositoryRoot
	final Repository repository;
	final Map files = [:]
	final Map plugins = [:];
	int commitCount = 0;

	RepositoryScript(Repository repository) {
		assert repository;

		this.repository = repository;
		this.repositoryRoot = repository.getDirectory().getParentFile();
	}

	/**
	 * Registriert die Plug-in Klasse (pluginName) unter der übergebenen id
	 * 
	 * Die Klasse muss über den Classloader gefunden werden und muss einen Konstruktor haben,
	 * dem diese RepositoryScript-Instanz übergeben wird
	 */
	def usePlugin(String id, String pluginName) {
		log.info "Loading Plug-in '$pluginName' as $id"

		final Class pluginClass = Class.forName(pluginName);
		def plugin = pluginClass.newInstance(this)
		plugins.put(id, plugin);
	}

	def propertyMissing(String propertyName) {
		def plugin = plugins[propertyName];

		if (plugin == null) {
			throw new IllegalStateException("Property '$propertyName' not found. Missing Plug-in import?");
		}

		return plugin;
	}

	//	def methodMissing(String methodName, args) {
	//		log.error "METHOD '$methodName' missing with args: $args"
	//
	//		for (plugin in plugins) {
	//			if (plugin.metaClass.respondsTo(plugin, methodName, args)) {
	//				return plugin.metaClass.invokeMethod(plugin,methodName, args);
	//			}
	//		}
	//
	//		 throw new MissingMethodException(methodName, this.class, args)
	//
	//	}

	def addFile(Map args, String id=null) {

		if (!id) {
			id = "f${files.size()+1}"
		}

		// Sicherstellen, dass es noch keine Datei mit dieser Id gibt
		assert ! files[id]

		final String locationInRepo = args.get('path', '');
		final String content = args.get('content', "Line1${ln}Line2${ln}Line3${ln}");

		log.info "Create File '$locationInRepo' with id '$id'"

		RepositoryFile repositoryFile = new RepositoryFile(repositoryRoot, locationInRepo, content);
		files.put id, repositoryFile


		return repositoryFile
	}

	def modifyFile(Map args=new Hashtable(), String id) {
		RepositoryFile repoFile = files[id];
		assert repoFile;

		if (args.content) {
			repoFile.writeContent(args.content);
		}

		if (args.add) {
			repoFile << args.add

			if (!args.add.endsWith(RepositoryScript.ln)) {
				repoFile << RepositoryScript.ln
			}
		}

		if (!args.content && !args.add) {
			repoFile << "A new line%n"
		}

		return repoFile;
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
			message = "[${repository.branch}] Commit No $commitCount";
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
	 * <li>create</li>: false: branch wird nicht angelegt, wenn er noch nicht exisitert, gibt es einen Fehler 
	 * (Default: true) 
	 * <li>startPoint</li>: Legt fest, wo der neue Branch beginnen soll (default: HEAD)
	 * <li>mergeCurrentBranch</li>: Mergt den aktuellen Branch nach dem auschecken des übergebenen Branches mit 'no-ff'
	 * <li>mergeCommitMessage</li>: Commit-Message für den Merge, falls mergeCurrentBranch auf 'true' steht
	 * </ul>
	 * @param branchName Der Name des Branches
	 * @return
	 */
	def checkout(Map args=new Hashtable(), String branchName) {
		final Git git = new Git(repository);
		final boolean orphan = args.get('orphan', false);
		final String startPoint = args.get('startPoint', 'HEAD')
		boolean createBranch = args.get('create', true)
		final def branches = git.branchList().call()
		final def refName = "refs/heads/" + branchName;
		final boolean mergeCurrentBranch = args.get('mergeCurrentBranch');
		final String currentBranch = repository.getBranch();

		if (createBranch) {
			// Prüfen, ob Branch angelegt werden muss
			for (branch in branches) {
				if (refName == branch.getName()) {
					createBranch = false;
					break;
				}
			}
		}

		log.info "${createBranch?'Erzeuge':'Aktiviere'} Branch '$branchName'. Start.Point: '$startPoint', orphan: $orphan."

		git.checkout().setCreateBranch(createBranch).
				setStartPoint(startPoint).
				setOrphan(orphan).setName(branchName).call();


		if (mergeCurrentBranch) {
			merge(currentBranch, message: args.get('mergeCommitMessage'));
		}
	}

	def deleteBranches(String... branchNames) {
		assert branchNames

		Git git = new Git(repository);
		git.branchDelete().setBranchNames(branchNames).setForce(false).call();
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
		String message = args.get('message');
		boolean noff = args.get('noff', true);
		MergeStrategy mergeStrategy = MergeStrategy.get(args.get('strategy', 'recursive'))
		assert mergeStrategy


		ObjectId ref = repository.resolve("refs/heads/$branchName")

		if (!message) {
			message = "Merge Branch '$branchName' into '${repository.getBranch()}'"
		}

		log.info "Merge Branch '$branchName' (${ref.abbreviate(5).name()}) into '${repository.getBranch()}'. No-FastForward: $noff"

		final Git git = new Git(repository);
		MergeResult result = git
				.merge()
				.setStrategy(mergeStrategy)
				.include(ref)
				.setCommit(false)
				.setFastForward(noff?FastForwardMode.NO_FF:FastForwardMode.FF)
				.call();
		assert result.mergeStatus.successful

		return git.commit().setMessage(message).call();
	}

	def tag(Map args=new Hashtable(), String tagName) {
		Git git = new Git(repository);
		git.tag().setName(tagName).call();
	}
}
