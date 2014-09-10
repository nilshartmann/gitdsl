package example
import gitdsl.GitRepository

// Neues Repository in /tmp/testgitflow anlegen
// ACHTUNG! Das Verzeichnis wird GELÖSCHT, wenn es schon vorhanden ist
GitRepository.recreateAt("/tmp/testgitflow").setup {

	// Plugin "importieren". Das Plug-in steht dann unter 'gf' zur Verfügung
	usePlugin 'gf', 'gitdsl.plugins.gitflow.GitFlowPlugin'

	// Initiales Repository erzeugen. Muss als erstes passieren.
	// In dem (optionalen) übergebenem Closure kann die Default-Konfiguration angepasst werden
	gf.init {
		// Git Flow Branches konfigurieren.
		// Wenn nicht explizit konfiguriert, wird der GitFlow Standard verwendet
		branches.master = 'prod';
		branches.feature = 'topic';
	}

	// startFeature erzeugt den Feature-Branch und eine Anzahl an Commits in file1
	gf.startFeature 'F_001', commits: 4
	gf.startFeature 'F_002', commits: 3
	gf.startFeature 'F_003', commits: 3

	// finishFeature mergt den Feature-Branch zurück auf 'develop' und löscht den Feature-Branch
	// (um den Feature-Branch zu behalten als Argument removeBranch: false angeben)
	gf.finishFeature 'F_002'
	gf.finishFeature 'F_001'

	// Beginnt mit der "codefreeze"-Phase für ein Release: erzeugt den release-Branch und modifiziert
	// darauf die 'version.txt'-Datei
	gf.startRelease 'v_0.1'

	// Feature-Branches auf dem Release-Branch?
	// Todo: weitere Commits auf einem Release-Branch erzeugen?!

	gf.finishFeature 'F_003' // Feature wird es für 'nächstes' Release fertig

	// Schliesst das Release ab: no-ff merge auf 'master' und develop, sowie tag des master-Branches
	gf.finishRelease 'v_0.1'


	gf.startFeature 'F_004'

	// ---- Nächster Releasezyklus, enthält zusätzlich F_003 aber nicht F_004
	gf.startRelease 'v_0.2'

	// Ein Hotfix für das erste Release erzeugen (TODO: Mergen in den release-Branch, wenn der gerade aktiv ist?)
	gf.hotfix 'v_0.1.1', commits: 2

	// enthält NICHT den Hotfix v_v0.1.1 ....
	gf.finishRelease 'v_0.2'

}



