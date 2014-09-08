package example
import gitdsl.GitRepository;

// Neues Repository in /tmp/testrepo anlegen
// ACHTUNG! Das Verzeichnis wird GELÖSCHT, wenn es schon vorhanden ist
GitRepository.recreateAt("/tmp/testgitflow").setup {
	
	usePlugin 'gf', 'gitdsl.plugins.gitflow.GitFlowPlugin' 
	
	gf.hello();

	gf.branches.master = 'releases';	
	
	
}