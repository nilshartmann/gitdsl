package scripts.wjax.merges

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=MERGES_BASE_DIR;


final def SQUASH_REPO="${BASE_DIR}/25_squash"
Utils.recreateFile("$BASE_DIR/beispiel_25_squash.txt") << """

# Beispiel: Squash Merge

	cd $SQUASH_REPO

	# Branches zeigen: master, feature-1 und feature-2; feature-1 bereits in master gemergt

    git merge --squash feature-2

	# Status zeigen

	# Commit
	git commit

 	# Neue Historie zeigen
	gitk -all


"""
GitRepository.recreateAt("${SQUASH_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt', content: 'Beschreibung Ihres Produktes'
	commit 'Initial Import'

	commits delegate, 'feature-1', subject: 'Datenbankzugriff optimieren', content: 'Beschreibung Ihres Produktes'
	commits delegate, 'feature-2', commits: 1, subject: 'REST-API implementieren', content: 'Beschreibung Ihres Produktes'
	commits delegate, 'feature-2', subject: 'REST-API implementieren', add: 'Feature-2 Aenderung'

	checkout 'master', create: false

	merge 'feature-1'
}