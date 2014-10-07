package scripts.wjax.merges

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=MERGES_BASE_DIR;

final def LOKALES_REPO="${BASE_DIR}/10_ff-noff"

Utils.recreateFile("$BASE_DIR/beispiel_10_ff-noff.txt") << """
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

GitRepository.recreateAt("${LOKALES_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, 'master'
}
