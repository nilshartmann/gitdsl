package scripts.wjax.merges

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=MERGES_BASE_DIR;

final def CONFLICT_REPO="${BASE_DIR}/40_conflicts"
Utils.recreateFile("$BASE_DIR/beispiel_40_conflicts.txt") << """

	# Ausgangssituation mit gitk zeigen: Zwei Branches, beide vom Master, beide README veraendert
	
	# Merge feature-1
	git merge --no-ff feature-1
	git branch -d feature-1	

	# ggf. Historie zeigen
	git merge --no-ff feature-2

	# MERGE KONFLIKT!!!

	# git status => wie erwartet
	git status

	# Lösung 1: Reset und 'ours' nehmen
	git reset --hard
	# oder: git merge --abort

	git merge -s ours feature-2

	# readme zeigen: enthaelt nur Aenderungen von feature-1
	cat readme.txt

	# Doof: install.txt ueberschrieben
	cat install.txt
	git diff feature-2 -- install.txt

	# Lösung: Nur Konflikt-behaftete Dateien von "uns" übernehmen
	git merge -X ours feature-2


	# Lösung 2: Nur readme.txt ignorieren
	git reset --hard HEAD^

	git merge feature-2
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

	git config merge.conflictstyle diff3


	git mergetool

	# Gestaged
	git status

	# Merge abschliessen
	git commit

	

	# ggf git rerere


	TODO: conflictstyle ... dabei auch die Konfliktmarker erklären
	TODO: 


	# Files anzeigen: 3 Stueck ???
	git ls-files --stage

"""
GitRepository.recreateAt("${CONFLICT_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'
	// Initialer Commit
	addFile 'f1', path: 'readme.txt', content: "Dieses ist die initiale Readme-Datei\nDa es in diesem Stadium noch keine Features gibt,ist sie leer\n"
	addFile 'f2', path: 'install.txt', content: 'Dieses wird die Installationsanleitung'
	commit 'Initial Import'

	commits delegate, 'feature-1', commits: 1, content: "Dieses ist die erstmals angepasste Readme-Datei\nBeschreibung von feature-1:\nDieses brandneue Feature erlaubt es Ihnen, noch besser mit unserer Software zu arbeiten."
	commits delegate, 'feature-2', commits: 1, content: "Dieses ist die Readme-Datei\nSie beschreibt alle Features Ihres gekauften Produktes\n\nNeu! feature-2:\nJetzt ist unser Tool noch besser.\nSie sollten es unbedingt sofort installieren und testen."
	modifyFile 'f2', add: 'Zur Installation gehen Sie wie folgt vor:\n\n 1. Lorem\n 2. Ipsum\n 3.Dolor sit amend'; commit "feature-2: install.txt erweitert"
	addFile 'f3', path: 'version.txt', content: 'Version 2'; commit "feature-2: version.txt angelegt"

	checkout 'master', create: false

}