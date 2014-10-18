package scripts.wjax.jgit

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=GITFLOW_BASE_DIR;

final def LOKALES_REPO="${BASE_DIR}/10_simple"

Utils.recreateFile("$BASE_DIR/beispiel_10_ff-noff.txt") << """
	cd ${LOKALES_REPO}

"""

GitRepository.recreateAt("${LOKALES_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt', content: """README
==================
Dieses ist die Readme-Datei mit vielen wichtigen Informationen

"""
	addFile 'f2', path: 'src/de/gitworkshop/Main.java', content: 'Main-Klasse'
	addFile 'f3', path: 'src/de/gitworkshop/impl/Service.java', content: 'Service-Klasse'
	addFile 'f4', path: 'src/de/gitworkshop/Util.java', content: 'Util-Klasse'
	addFile 'f5', path: 'src-test/de/gitworkshop/Test.java', content: 'Test-Klasse'
	commit 'Initial Import'

	addFile 'f6', path: 'version.txt', content: "v1.0"
	commit 'version.txt hinzugefuegt'

	checkout 'feature/tests_erweitern'

	modifyFile 'f5', add: """
public class Test extends TestCase {
}
"""
	modifyFile 'f1', add: '- Zum Ausfuehren der Tests Test starten'
	commit '1. Testklasse angelegt'

	modifyFile 'f5', content: """
package de.gitworkshop;
public class Test extends TestCase {
	@Test
 public void myTest() { }
}
"""
	commit '2. Testklasse erweitert'

	modifyFile 'f5', content: """
package de.gitworkshop;
public class Test extends TestCase {
	@Test
    public void myTest() { 
		assertTrue(true);
	}
}
"""
	commit '3. Testklasse fertig'
}
