package de.gitworkshop.web;

public class GitCommand {
	
	private final String name;
	private final String description;
	public GitCommand(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

}
