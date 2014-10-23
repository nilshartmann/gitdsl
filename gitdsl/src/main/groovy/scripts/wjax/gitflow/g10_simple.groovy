package scripts.wjax.gitflow

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=GITFLOW_BASE_DIR;

final def UEBUNG_REPO="${BASE_DIR}/gitflow-uebung"
final def BEISPIEL_REPO="${BASE_DIR}/gitflow-beispiel"

Utils.recreateFile("$BASE_DIR/beispiel_gitflow.txt") << """
	cd ${BEISPIEL_REPO}

	# Releases zeigen
	git tag -l

	# Aktive Features zeigen
	git branch --list feature/*

	# Welche Features sind in v2.0
	git log --oneline --first-parent v2.0^..v2.0^2


"""

Utils.recreateFile("$BASE_DIR/uebung-gitflow.txt") << """
	cd ${UEBUNG_REPO
}

1. Wieviele Releases sind bereits veroeffentlicht worden?
	git tag -l

	LÖSUNG: v0.1 - v0.4
2. Welche Features sind gerade in arbeit?
	git branch --list feature/*
    ODER
	git branch -l
	
	LÖSUNG: migrateToJava8

3. Welche Features sind bereits für das kommende Release gemergt?
	gitk
	ODER
	git log --first-parent --oneline \$(git merge-base HEAD master)..HEAD --grep feature

	LÖSUNG: 2 Features: implementTwoFactorAuthentication removeJavaFXClient

5. Erzeugen Sie mit den bereits gemergten Features ein neues Release. Setzen Sie dabei 
   in der Datei version.txt die korrekte Versionsnummer für das neue Release

   git checkout -b release/v0.5
   echo \"Version 0.5\">version.txt
   git commit -m \"Version 0.5 angelegt\" version.txt
   git checkout master
   git merge --no-ff release/v0.5 -m \"Release 0.5 fertiggestellt\"
   git merge --no-ff -m \"Release 0.5 in Entwicklungslinie\" release/v0.5
   git branch -d v0.5

6. [ZUGABE] Machen Sie für das Release 0.2 einen Backport von feature/fixShellshockSecurityIssue 
	(Dabei werden u.U. Merge-Konflikte auftreten. Diese brauchen Sie nicht zu beheben)

	git checkout -b backport v0.2
	git log --oneline --grep fixShellshockSecurityIssue develop
	
	git cherry-pick 4593d06..2cd83fc
	git checkout --theirs readme.txt
	git add readme.txt
	git cherry-pick --continue

	git tag v0.2.1
	git branch -D backport
	
	OPTIONAL: Backport-Branch löschen  

"""

GitRepository.recreateAt("${UEBUNG_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	addFile 'f2', path: 'version.txt', content: 'HEAD'
	commit 'Initial Import'

	checkout 'develop'


	commits delegate, 'feature/implementRestApi', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/improveSecurity', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/replaceMavenWithGradle', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true



	checkout 'release/v0.1'

	modifyFile 'f2', content: 'Version 0.1'; commit "Release 0.1 fertig"

	checkout 'master', create:false
	merge 'release/v0.1'
	tag 'v0.1'
	checkout 'develop'
	merge 'release/v0.1'
	deleteBranches 'release/v0.1'

	// Release 0.2
	commits delegate, 'feature/upgradeSpringVersion', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/inspectPerformance', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true

	checkout 'release/v0.2'
	modifyFile 'f2', content: 'Version 0.2'; commit "Release 0.2 fertig"
	checkout 'master', create:false
	merge 'release/v0.2'
	tag 'v0.2'
	checkout 'develop'
	merge 'release/v0.2'
	deleteBranches 'release/v0.2'

	// Release 0.3
	commits delegate, 'feature/implementExperimentalJavaFXClient', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/extendTestSuites', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true, commits: 4
	commits delegate, 'feature/fixShellshockSecurityIssue', commits: 4, startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true

	checkout 'release/v0.3'
	modifyFile 'f2', content: 'Version 0.3'; commit "Release 0.3 fertig"
	checkout 'master', create:false
	merge 'release/v0.3'
	tag 'v0.3'
	checkout 'develop'
	merge 'release/v0.3'
	deleteBranches 'release/v0.3'

	// Release 0.4 -
	commits delegate, 'feature/implementOAuth', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/enhanceJavaFXClient', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true, commits:2
	commits delegate, 'feature/rewriteJavadoc', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true, commits:1

	checkout 'release/v0.4'
	modifyFile 'f2', content: 'Version 0.4'; commit "Release 0.4 fertig"
	checkout 'master', create:false
	merge 'release/v0.4'
	tag 'v0.4'
	checkout 'develop'
	merge 'release/v0.4'
	deleteBranches 'release/v0.4'

	// Release 0.5 (noch nicht veröffentlicht)

	commits delegate, 'feature/migrateToJava8', startPoint: 'develop', commits: 5
	commits delegate, 'feature/removeJavaFXClient', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true, commits:1
	commits delegate, 'feature/implementTwoFactorAuthentication', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true

}

// ============================= BEISPIEL ==============================================================


GitRepository.recreateAt("${BEISPIEL_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	// Initialer Commit
	addFile 'f1', path: 'readme.txt'
	addFile 'f2', path: 'version.txt', content: 'HEAD'
	commit 'Initial Import'

	checkout 'develop'


	commits delegate, 'feature/allowUserLoginWithGoogleId', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/enhancePrivacyOptions', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/fixTransactionBehaviour', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true



	checkout 'release/v1.0'

	modifyFile 'f2', content: 'Version 1.0'; commit "Release 1.0 fertig"

	checkout 'master', create:false
	merge 'release/v1.0'
	tag 'v1.0'
	checkout 'develop'
	merge 'release/v1.0'
	deleteBranches 'release/v1.0'

	// Release 0.2
	commits delegate, 'feature/implementPrivateMessages', startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/implementShareWithActions', commits: 7, startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/addIndividualUserGravatars', commits: 2, startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true
	commits delegate, 'feature/overhaulSecurityAudits', commits: 2, startPoint: 'develop', mergeTo: 'develop', deleteAfterMerge: true

	checkout 'release/v2.0'
	modifyFile 'f2', content: 'Version 2.0'; commit "Release 2.0 fertig"
	checkout 'master', create:false
	merge 'release/v2.0'
	tag 'v2.0'
	checkout 'develop'
	merge 'release/v2.0'
	deleteBranches 'release/v2.0'

	// Release 0.3
	commits delegate, 'feature/addTwitterIntegration', startPoint: 'develop', commits: 4
	commits delegate, 'feature/fixLogoutButtonStyle', startPoint: 'develop', commits: 1

	checkout 'develop', create: false
}
