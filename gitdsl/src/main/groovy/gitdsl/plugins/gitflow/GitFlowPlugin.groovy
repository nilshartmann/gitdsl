package gitdsl.plugins.gitflow

import gitdsl.RepositoryScript;
import groovy.util.logging.Log4j2;

@Log4j2
class GitFlowPlugin {
	
	private final RepositoryScript repositoryScript;
	GitFlowBranches branches = new GitFlowBranches();
	
	GitFlowPlugin(RepositoryScript repositoryScript) {
		log.info "GitFlowPlugin, repositoryScript: $repositoryScript"
		
		this.repositoryScript = repositoryScript;
	}
	
	def hello(String greeting='world') {
		log.info("HELLO $greeting")
	}
	

}
