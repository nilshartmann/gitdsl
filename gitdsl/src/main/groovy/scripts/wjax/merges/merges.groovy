//
package scripts.wjax.merges
import java.nio.file.Files;

import gitdsl.GitRepository
import gitdsl.Utils;

final def BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/uebungen/merges';



final def LOKALES_REPO="${BASE_DIR}/01_ffRepo"

Utils.recreateFile("$BASE_DIR/beispiel1_fastforward.txt") << """
SZENARIO 1: FF-MERGE

	cd ${LOKALES_REPO}

	# Bestehende Branches und Historie zeigen
	git branch -a
	git log

	# Einen Branch erzeugen
	git checkout -b neues_feature
	
	# Datei aendern und committen
	echo "Eine Aenderung" >>readme.txt && git commit -m "Eine Aenderung auf Branch 'neues_feature'" readme.txt

	# Zurueck zum master wechseln
	git checkout master
	
	# Aenderung mergen
	git merge neues_feature

	# FF-Merge, Ausgabe und neue Historie zeigen

	# Feature Branch löschen
	git branch -d feature

	# Zweites Feature
	git checkout -b feature2
	echo "Eine weitere Aenderung" >>readme.txt && git commit -m "Eine Aenderung auf Branch 'feature2'" readme.txt

	# Zurueck auf master
	git merge --no-ff feature2

	
	# ggf. zwei weitere Branches anlegen und -ff-only zeigen

"""

def commits(Map args = new Hashtable(), gitdsl.RepositoryScript gs, String branch) {
	gs.checkout branch, startPoint: 'master';

	for (int i=1;i<4;i++) {
		gs.modifyFile 'f1'; gs.commit "$branch: $i. Commit";
	}


	String mergeTo = args.get("mergeTo");
	if (mergeTo) {
		gs.checkout mergeTo
		gs.merge branch, message: "Finished $branch: '$args.subject' (Merged into $mergeTo)"

	}
}

GitRepository.recreateAt("${LOKALES_REPO}").setup {
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'master'
	checkout 'master', create: false
}

// ===== BEISPIEL 2 ==================================================================
final def FIRST_PARENT_REPO="${BASE_DIR}/02_firstParentRepo"
Utils.recreateFile("$BASE_DIR/beispiel_firstparent.txt") << """

# Beispiel: First Parent History

	cd $FIRST_PARENT_REPO

	# "Normale" Historie mit Git Log zeigen
	git log

	# First-Parent Historie zeigen
	git log --first-parent --oneline

"""
GitRepository.recreateAt("${FIRST_PARENT_REPO}").setup {
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'FEATURE-1', mergeTo: 'master', subject: 'Datenbankzugriff optimieren'
	commits delegate, 'FEATURE-2', mergeTo: 'master', subject: 'Benutzerverwaltung ueber LDAP'
	commits delegate, 'FEATURE-3', mergeTo: 'master', subject: 'REST-Schnittstelle'
	commits delegate, 'FEATURE-4', mergeTo: 'master', subject: 'Test-Suite erweitert'

	checkout 'master', create: false

	deleteBranches 'feature-1', 'feature-2', 'feature-3', 'feature-4'

}



