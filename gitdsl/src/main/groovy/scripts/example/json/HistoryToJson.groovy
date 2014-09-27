package scripts.example.json
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit;

final def INPUT_REPO = "/tmp/testgitflow"

File gitDir = new File("${INPUT_REPO}/.git");

Repository repository = new FileRepository(gitDir);

Git git = new Git(repository)

Iterable<RevCommit> completeLog = git.log().all().call()

StringBuilder builder = new StringBuilder();
StringBuilder links = new StringBuilder();
StringBuilder labels = new StringBuilder();

completeLog.each {

	def commitId = it.id.abbreviate(7).name();
	def commitIdVeryShort = it.id.abbreviate(3).name();
	def msg = it.shortMessage.replace('\'', '');

	// { id: 'node1', value: { label: 'node1' } },
	builder.append("\n{ id: '$commitId', value: { label: '$commitIdVeryShort' } },");

	labels.append("\n'$commitId': '$msg',")

	it.parents.each() { parent ->
		def myCommitId = parent.id.abbreviate(7).name()
		//{ u: 'node1', v: 'node2', value: { label: 'link1' } },
		links.append("\n{ u: '$myCommitId', v: '$commitId', value: { label: '' } },");
	}

	println "$commitId $msg"
}

builder.deleteCharAt(builder.length()-1);
links.deleteCharAt(links.length()-1);
println builder

println links

def json = """loadData(
    {
        name: 'git-history',
		labels: { $labels },
        nodes: [ $builder ],
		links: [ $links ]
}
);
"""

File out = new File('/Users/nils/develop/wjax2014_git_workshop/d3-dag-visualization/app/graph3.js')
if (!out.exists()) {
	out.createNewFile()
}

out.write(json.toString());