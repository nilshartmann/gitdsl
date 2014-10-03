package scripts.wjax.merges

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=MERGES_BASE_DIR;

final def CONFLICT_REPO="${BASE_DIR}/04_conflicts"
Utils.recreateFile("$BASE_DIR/beispiel_04_conflicts.txt") << """

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
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'
	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	addFile 'f2', path: 'install.txt', content: 'Dieses wird die Installationsanleitung'
	commit 'Initial Import'

	commits delegate, 'FEATURE-1', commits: 1, content: 'Aenderung auf FEATURE-1'
	commits delegate, 'FEATURE-2', commits: 1, content: 'Aenderung auf FEATURE-2'
	modifyFile 'f2', add: 'Zur Installation gehen Sie wie folgt vor:\n\n 1. Lorem\n 2. Ipsum\n 3.Dolor sit amend'; commit "FEATURE-2: install.txt erweitert"

	checkout 'master', create: false

}