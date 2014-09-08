package gitdsl

import java.io.File;

/**
 * Repräsentiert eine Datei im Repository, die im Rahmen des Setups angelegt wurde
 * @author nils
 *
 */
class RepositoryFile {
	
	
	final File repositoryRoot;
	
	/** Der relative Pfad (zum Repository-Root) der Datei */
	String locationInRepository;
	
	/** Die Datei im Filesystem */
	private File file
	
	RepositoryFile(File repositoryRoot, String locationInRepository, String initialContent = null) {
		this.repositoryRoot = repositoryRoot;
		
		this.locationInRepository = removeLeadingSlash(locationInRepository);
		this.file = createFile(locationInRepository);
		
		// (Leere) Datei anlegen
		this.file.createNewFile();
		
		if (initialContent) {
			writeContent initialContent;
		}
		
	}
	
	/**
	 * Setzt den Inhalt dieser Datei
	 */
	def writeContent(String content) {
		if (content==null) {
			content = "";
		}
		
		file.text = content;
	}

	/** Verschiebt die Datei an eine neue Position. Die neue Position kann ein neues
	 * Verzeichnis und/oder ein neuer Name sein. Fehlende Verzeichnisse werden angelegt
	 *  
	 * @param newLocationInRepository Neuer (relativer) Pfad im Repository
	 * @param newContent Neuer Inhalt der Datei (optional)
	 * @return ALTER Pfad im Repository
	 */	
	String moveTo(String newLocationInRepository, String newContent = null) {
		final String oldLocationInRepo = this.locationInRepository;
		final File oldFile = this.file;
		
		this.locationInRepository = removeLeadingSlash(locationInRepository);
		this.file = createFile(locationInRepository);
		
		// Datei verschieben und ggf umbenennen
		boolean fileMoved = oldFile.renameTo(file)
		assert fileMoved
		
		if (newContent != null) {
			writeContent(newContent)
		}
		
		return oldLocationInRepo;
		
	}
	
	// ------------------------------------------------------------------------------------------
	private static String removeLeadingSlash(String path) {
		if (path.startsWith('/')) {
			assert path.length() > 1
			path = path.substring(1);
		}
		
		return path;
	}
	
	private File createFile(String locationInRepository) {
		File newFile = new File(repositoryRoot, locationInRepository);
		newFile.getParentFile().mkdirs();
		
		return newFile;
	}
	
}
