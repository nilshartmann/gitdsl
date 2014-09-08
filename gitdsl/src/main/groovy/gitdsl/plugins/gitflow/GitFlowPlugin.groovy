package gitdsl.plugins.gitflow

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.RefUpdate

import gitdsl.RepositoryScript;
import groovy.util.logging.Log4j2;

@Log4j2
class GitFlowPlugin {
	
	private final RepositoryScript repositoryScript;
	private boolean initialized = false;
	GitFlowBranches branches = new GitFlowBranches();
	
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
		Git git = new Git(repositoryScript.repository);
		RefUpdate refUpdate = repositoryScript.repository.getRefDatabase().newUpdate(Constants.HEAD, false);
		refUpdate.setForceUpdate(true);
		refUpdate.link(Constants.R_HEADS + branches.master);
		git.commit().setMessage("Initial Commit").call();
		
		// Initialen 'develop'-Branch anlegen
		log.info("Erzeuge GitFlow 'develop'-Branch für die Entwicklung als '$branches.develop'");
		git.branchCreate().setName(branches.develop).call();
	}
	
	
	def hello(String greeting='world') {
		log.info("HELLO $greeting")
	}
	

}
