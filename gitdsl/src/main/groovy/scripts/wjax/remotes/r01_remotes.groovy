package scripts.wjax.remotes
import static scripts.wjax.WJaxUtils.*;
import gitdsl.GitRepository
import gitdsl.Utils;


final def LOKALES_REPO_EINS="${REMOTES_BASE_DIR}/lokalesRepo"
final def SPRING_REPO="${REMOTES_BASE_DIR}/spring.git"
final def SPRING_KLON_REPO="${REMOTES_BASE_DIR}/mein-spring-klon.git"

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
