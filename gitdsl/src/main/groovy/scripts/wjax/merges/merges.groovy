//
package scripts.wjax.merges
import java.nio.file.Files;

import gitdsl.GitRepository
import gitdsl.Utils;

final def BASE_DIR='/Users/nils/develop/wjax2014_git_workshop/uebungen/merges';



final def LOKALES_REPO="${BASE_DIR}/01_ffRepo"

Utils.recreateFile("$BASE_DIR/beispiel_01_fastforward.txt") << """
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

	final int commits = args.get('commits', 3);

	for (int i=1;i<(commits+1);i++) {
		gs.modifyFile 'f1', content: args.content; gs.commit "$branch: $i. Commit";
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
Utils.recreateFile("$BASE_DIR/beispiel_02_firstparent.txt") << """

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

// ===== BEISPIEL 2 ==================================================================
final def CONFLICT_REPO="${BASE_DIR}/03_mergeConflictRepo"
Utils.recreateFile("$BASE_DIR/beispiel_03_mergeConflict.txt") << """

	# Ausgangssituation mit gitk zeigen: Zwei Branches, beide vom Master, beide README veraendert
	
	# Merge FEATURE-1
	git merge --no-ff FEATURE-1
	git branch -d FEATURE-1	

	# ggf. Historie zeigen
	git merge --no-ff FEATURE-2

	# MERGE KONFLIKT!!!

	# git status => wie erwartet
	git status

	# Lösung 1: Reset und 'ours' nehmen
	git reset --hard
	# oder: git merge --abort

	git merge -s ours FEATURE-2

	# readme zeigen: enthaelt nur Aenderungen von FEATURE-1
	cat readme.txt

	# Doof: install.txt ueberschrieben
	cat install.txt
	git diff FEATURE-2 -- install.txt

	# Lösung: Nur Konflikt-behaftete Dateien von "uns" übernehmen
	git merge -X ours FEATURE-2


	# Lösung 2: Nur readme.txt ignorieren
	git reset --hard HEAD^

	git merge FEATURE-2
	git checkout --ours -- readme.txt
	
	# Status zeigen (readme.txt nicht gestaged), aber conflikt marker sind weg
	cat readme.txt

	# mit checkout HEAD wuerde auch gleich gestaged
	# git checkout HEAD -- readme.txt
	
	# oder git checkout --ours -- readme.txt
	git status
	# readme.txt ist weg...

	# Lösung 3: readme.txt überschreiben
	git checkout -m -- readme.txt
	
	# Konfliktmarker wieder da!
	cat readme.txt

	# 'andere' Version nehmen
	git checkout --theirs -- readme.txt

	# git mergetool
	git checkout -m -- readme.txt

	git mergetool

	# Gestaged
	git status

	# Merge abschliessen
	git commit

	

	# ggf git rerere


	TODO: conflictstyle ... dabei auch die Konfliktmarker erklären
	TODO: 


	# Files anzeigen: 3 Stueck ???
	git ls-files 
	git ls-files --stage



	


"""
GitRepository.recreateAt("${CONFLICT_REPO}").setup {
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	addFile 'f2', path: 'install.txt', content: 'Dieses wird die Installationsanleitung'
	commit 'Initial Import'

	commits delegate, 'FEATURE-1', commits: 1, content: 'Aenderung auf FEATURE-1'
	commits delegate, 'FEATURE-2', commits: 1, content: 'Aenderung auf FEATURE-2'
	modifyFile 'f2', add: 'Zur Installation gehen Sie wie folgt vor:\n\n 1. Lorem\n 2. Ipsum\n 3.Dolor sit amend'; commit "FEATURE-2: install.txt erweitert"

	checkout 'master', create: false

}

