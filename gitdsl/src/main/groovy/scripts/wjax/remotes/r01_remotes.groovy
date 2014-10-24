package scripts.wjax.remotes
import static scripts.wjax.WJaxUtils.*;
import gitdsl.GitRepository
import gitdsl.Utils;


final def LOKALES_REPO_EINS="${REMOTES_BASE_DIR}/beispiel/lokalesRepo"
final def SPRING_REPO="${REMOTES_BASE_DIR}/beispiel/spring.git"
final def SPRING_KLON_REPO="${REMOTES_BASE_DIR}/beispiel/mein-spring-klon.git"

final def UEBUNG_REPO="${REMOTES_BASE_DIR}/uebung/spring.git"
final def UEBUNG_KLON_REPO="${REMOTES_BASE_DIR}/uebung/mein-spring.git"

final beispielRemotesTxt = Utils.recreateFile("$REMOTES_BASE_DIR/beispiel_01_remotes.txt");
beispielRemotesTxt << """

# Meinen eigenen Klon lokal klonen
git clone file://${SPRING_KLON_REPO} mein-lokales-spring-repo

git remote -v
git ls-remote


git checkout 4.0.x
# readme veraendern und committen

git remote add upstream file://${SPRING_REPO}
git ls-remote origin refs/heads/*
git ls-remote upstream refs/heads/*

git fetch upstream
git merge upstream/4.0.x

git push
# -> Pusht zum origin

git push upstream
# -> pusht zum upstream

# Alternative: pushurl in git config setzen
git config remote.origin.pushurl file://${SPRING_REPO}

# Einen Commit machen
git push
# -> pusht zum upstream

# push-url zuruecksetzen
git config --unset remote.origin.pushurl
git remote rm upstream



"""

GitRepository.recreateAt("${SPRING_REPO}.tmp").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, '3.1.x'
	commits delegate, '3.2.x'
	commits delegate, '4.0.x'

	checkout 'master', create: false
}
.createAsBareAt("${SPRING_KLON_REPO}").
setup {
	checkout '4.0.x', create: false
	addFile 'f2', path: 'NewFactoryBean.java', content: """
public class NewFactoryBean {
   // let's go...
}
"""
	commit 'Added NewFactoryBean for Spring v4'
}
.createAsBareAt("${SPRING_REPO}", deleteSource: true)

// ================================== UEBUNG =====================================
GitRepository.recreateAt("${UEBUNG_REPO}.tmp").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	commit 'Initial Import'

	commits delegate, '3.1.x'
	commits delegate, '3.2.x'
	commits delegate, '4.0.x'

	checkout 'master', create: false

	// Eine Datei auf dem Feature-Branch anlegen
	checkout 'feature-1'
	addFile 'f2', path: 'NewFactoryBean.java', content: """
public class NewFactoryBean {
   // let's go...
}
"""
	commit 'Feature-1: Begonnen mit Implementierung'

	checkout 'master', create: false

}
.createAsBareAt("${UEBUNG_KLON_REPO}").
setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	checkout '4.1.x', startPoint: 'master'; addFile path: 'readme-4.1.x'; commit "Commit auf 4.1.x"

	// Einen weiteren Commit auf feature-1 um ff-push zu verhindern
	checkout 'feature-1', create: false

	addFile 'f2', path: 'NewFactoryBean.java', content: """
public class NewFactoryBean {
   public NewFactoryBean() {
   }
}
"""
	commit "Feature-1 im zentralen Repo: Constructor implementiert"
	checkout 'master', create: false


}
.createAsBareAt("${UEBUNG_REPO}")
.setup {


}

Utils.deleteDirectory("${UEBUNG_REPO}.tmp")
