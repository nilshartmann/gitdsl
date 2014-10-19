package de.gitworkshop.web;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.LogManager;

public class GitService {
	
	public List<GitCommand> getGitCommands() throws Exception {
		
        LogManager.getRootLogger().info("Reading Git Commands");
        InputStream gitCommandsIs = getClass().getResourceAsStream("/git-commands.txt");
        
        List<GitCommand> result = new LinkedList<GitCommand>();
        
        try {
        	LineIterator lineIterator = IOUtils.lineIterator(gitCommandsIs, null);
        	
        	while (lineIterator.hasNext()) {
        		String line = lineIterator.nextLine().trim();
        		int v = line.indexOf(' ');
        		String name = line.substring(0,v);
        		String description = line.substring(v).trim();
        		
        		LogManager.getRootLogger().info(" Command: '" + name + "' description: '" + description + "'");
        		
        		GitCommand gitCommand = new GitCommand(name, description);
        		result.add(gitCommand);
        	}
        	
        	return result;
        }
        finally {
           IOUtils.closeQuietly(gitCommandsIs);
        }
    }
}
