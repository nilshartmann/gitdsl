package gitdsl

import java.io.File;

class RepositoryFile {
	File file;
	String locationInRepository;
	
	RepositoryFile(File file, String locationInRepository) {
		this.file = file;
		this.locationInRepository = locationInRepository;
		
		file.getParentFile().mkdirs();
		file.createNewFile();
	}
	
	def writeContent(String content) {
		if (content==null) {
			content = "";
		}
		
		file.text = content;
	}
	
	def moveTo(File newFile, String newLocationInRepository) {
		this.locationInRepository = newLocationInRepository;

		newFile.getParentFile().mkdirs();
		
		boolean fileMoved = file.renameTo(newFile)
		assert fileMoved

		this.file = newFile;
	}
	
}
