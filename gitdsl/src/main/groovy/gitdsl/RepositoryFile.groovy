package gitdsl

import java.io.File;

class RepositoryFile {
	File file;
	String locationInRepository;
	
	RepositoryFile(File file, String locationInRepository) {
		this.file = file;
		this.locationInRepository = locationInRepository;
	}
	
	def writeContent(String content) {
		if (content==null) {
			content = "";
		}
		
		file.text = content;
		
	}
}
