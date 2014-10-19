package de.gitworkshop.web;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class GitServiceTest {
	
	@Test
	public void getGitCommandsReturnsList() throws Exception {
		
		GitService gitService = new GitService();
		List<GitCommand> gitCommands = gitService.getGitCommands();
		assertNotNull(gitCommands);
		
	}

}
