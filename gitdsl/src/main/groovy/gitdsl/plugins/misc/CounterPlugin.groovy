package gitdsl.plugins.misc

import gitdsl.RepositoryScript;
import groovy.util.logging.Log4j2;

@Log4j2
class CounterPlugin {

	private final Map<String, Integer> idRegistry = new Hashtable<>();

	CounterPlugin(RepositoryScript repositoryScript) {
		log.info "CounterPlugin, repositoryScript: $repositoryScript"
	}

	int next(String id) {
		int current = idRegistry.get(id, 0);
		current++;
		idRegistry.put(id, current);

		return current;
	}
}
