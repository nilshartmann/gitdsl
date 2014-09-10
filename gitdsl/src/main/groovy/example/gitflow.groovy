package example
import gitdsl.GitRepository

// Neues Repository in /tmp/testgitflow anlegen
// ACHTUNG! Das Verzeichnis wird GELÖSCHT, wenn es schon vorhanden ist
GitRepository.recreateAt("/tmp/testgitflow").setup {

	// Plugin "importieren". Das Plug-in steht dann unter 'gf' zur Verfügung
	usePlugin 'gf', 'gitdsl.plugins.gitflow.GitFlowPlugin'

	// Initiales Repository erzeugen. Muss als erstes passieren.
	// In dem (optionalen) übergenem Closure kann die Default-Konfiguration angepasst werden
	gf.init {
		// Git Flow Branches konfigurieren.
		// Wenn nicht explizit konfiguriert, wird der GitFlow Standard verwendet
		branches.master = 'releases';
		branches.feature = 'topic';
	}

	gf.startFeature 'F_001', commits: 4
	gf.startFeature 'F_002', commits: 3
	gf.startFeature 'F_003', commits: 3

	gf.finishFeature 'F_002'
	gf.finishFeature 'F_001'

	// gf.startRelease 'v_0.1' // <-- Was genau passiert hier, außer dass der Rel_Branch erzeugt wird?
	// gf.finishRelease 'v_0.1' // no-ff merge auf 'master', merge release develop





}



