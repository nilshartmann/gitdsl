package gitdsl.plugins.gitflow

import gitdsl.RepositoryScript
import groovy.util.logging.Log4j2

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.RefUpdate

@Log4j2
class GitFlowPlugin {

	private final RepositoryScript repositoryScript;
	private boolean initialized = false;
	private final List featureIds = []
	final GitFlowBranches branches = new GitFlowBranches();


	GitFlowPlugin(RepositoryScript repositoryScript) {
		log.info "GitFlowPlugin, repositoryScript: $repositoryScript"

		this.repositoryScript = repositoryScript;
	}

	def init(closure) {
		assert !initialized, 'Must initialize GitFlow Plug-in only once';

		log.info "Initialisiere GitFlow Umgebung"
		closure.delegate = this;
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure();


		// Initialen Commit anlegen
		// Uebernommen von: https://bitbucket.org/atlassian/jgit-flow-defunct/src/fcb0d1f52b4379b3a906fdebec443bf875212da0/jgit-flow-core/src/main/java/com/atlassian/jgitflow/core/JGitFlowInitCommand.java?at=develop#cl-216

		log.info("Erzeuge GitFlow 'master'-Branch für Releases als '$branches.master'")
		Git git = newGit();
		RefUpdate refUpdate = repositoryScript.repository.getRefDatabase().newUpdate(Constants.HEAD, false);
		refUpdate.setForceUpdate(true);
		refUpdate.link(Constants.R_HEADS + branches.master);

		repositoryScript.addFile 'README_FILE', path: 'README.txt', content: '''Initiales Readme-File
'''
		repositoryScript.commit 'Initial Commit'

		// Initialen 'develop'-Branch anlegen
		log.info("Erzeuge GitFlow 'develop'-Branch für die Entwicklung als '$branches.develop'");
		git.branchCreate().setName(branches.develop).call();
	}

	def startFeature(Map args=new Hashtable(), String featureId) {
		assert !featureIds.contains(featureId)

		final String featureBranch = branchNameForFeature(featureId);
		final int commits = args.get('commits', 3);
		assertWorkspaceClean();

		repositoryScript.checkout featureBranch, startPoint: branches.develop
		repositoryScript.addFile "${featureId}_f1", path:'file1.txt'
		repositoryScript.addFile "${featureId}_f2", path:'file2.txt'
		repositoryScript.addFile "${featureId}_f3", path:'file3.txt'

		repositoryScript.commit("Initialer Commit on Feature $featureId")

		for (int i = 1; i <= commits; i++) {
			repositoryScript.modifyFile "${featureId}_f1", add: "[$featureId] $i. Aenderung";
			repositoryScript.commit("${i}. Commit on Feature $featureId")
		}

		featureIds.add featureId
	}

	def finishFeature(Map args = new Hashtable(), String featureId) {
		assert featureIds.remove(featureId)
		assertWorkspaceClean();

		final boolean removeBranch = args.get('removeBranch', 'true')

		log.info "Finishing Feature '$featureId'"

		final String featureBranch = branchNameForFeature(featureId);
		repositoryScript.checkout branches.develop, create: false;
		repositoryScript.merge featureBranch, message: "Finish Feature $featureId", strategy: 'theirs'

		if (removeBranch) {
			Git git = newGit();
			git.branchDelete().setBranchNames(featureBranch).call();
		}
	}

	def startRelease(Map args=new Hashtable(), String releaseId) {
		assertWorkspaceClean()

		repositoryScript.checkout branchNameForRelease(releaseId), startPoint: branches.develop;

		repositoryScript.modifyFile 'README_FILE', add: """\
Start finishing Release ${releaseId}
"""
		repositoryScript.commit "Start finishing Release $releaseId"
	}

	def finishRelease(Map args=new Hashtable(), String releaseId) {
		assertWorkspaceClean()
		boolean removeBranch = args.get('removeBranch', true)

		repositoryScript.checkout branches.master, create: false

		// Auf den 'release'-Branch mergen und taggen
		repositoryScript.merge branchNameForRelease(releaseId), message: "Release $releaseId"
		repositoryScript.tag releaseId

		// Auf den 'develop'-Branch mergen
		repositoryScript.checkout branches.develop, create: false
		repositoryScript.merge branchNameForRelease(releaseId), message: "Release $releaseId back into develop"

		if (removeBranch) {
			Git git = newGit();
			git.branchDelete().setBranchNames(branchNameForRelease(releaseId)).call();
		}
	}

	private String branchNameForRelease(String releaseId) {
		assert releaseId
		return "${branches.release}/$releaseId"
	}


	private String branchNameForFeature(String featureId) {
		assert featureId;
		return "${branches.feature}/$featureId";
	}

	private def assertWorkspaceClean() {
		Git git = newGit();
		Status status = git.status().call();
		assert status.isClean();
	}

	private Git newGit() {
		return new Git(repositoryScript.repository);
	}


	def hello(String greeting='world') {
		log.info("HELLO $greeting")
	}
}
