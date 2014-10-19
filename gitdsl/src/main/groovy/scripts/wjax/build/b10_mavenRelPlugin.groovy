package scripts.wjax.build

import static scripts.wjax.WJaxUtils.*
import gitdsl.GitRepository;
import gitdsl.Utils;

import java.util.Map;

final def BASE_DIR=BUILD_BASE_DIR + "/maven-rel-plugin";

final def LOKALES_REPO="${BASE_DIR}/web-application"


GitRepository.recreateAt("${LOKALES_REPO}").setup {
	usePlugin 'counter', 'gitdsl.plugins.misc.CounterPlugin'

	copyExternalDirectory '/Users/nils/develop/wjax2014_git_workshop/git-repos/maventest/gitdsl/wjax-repositories/web-application'

	addFile 'f1', path: 'pom.xml'
	modifyFile 'f1', replace: ['SCM_REMOTE_REPO': "file://${LOKALES_REPO}.git"]

	commit "Initial Import"
}
.createAsBareAt("${LOKALES_REPO}.git", deleteSource: true)

final cloneZweiMal = Utils.recreateFile("$BASE_DIR/clone-app.sh");
cloneZweiMal.setExecutable(true);
cloneZweiMal << """#!/bin/bash

cd $BASE_DIR
rm -rf web-application

  git clone file:///${LOKALES_REPO}.git web-application

"""