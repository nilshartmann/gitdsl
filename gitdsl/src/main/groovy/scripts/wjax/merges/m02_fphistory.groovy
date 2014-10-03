package scripts.wjax.merges

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=MERGES_BASE_DIR;


final def FIRST_PARENT_REPO="${BASE_DIR}/02_first-parent"
Utils.recreateFile("$BASE_DIR/beispiel_02_first-parent.txt") << """

# Beispiel: First Parent History

	cd $FIRST_PARENT_REPO

	# "Normale" Historie mit Git Log zeigen
	git log

	# First-Parent Historie zeigen
	git log --first-parent --oneline

"""
GitRepository.recreateAt("${FIRST_PARENT_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

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