package gitdsl

import java.io.File;

/**
 * Repräsentiert eine Datei im Repository, die im Rahmen des Setups angelegt wurde
 * @author nils
 *
 */
class RepositoryFile {
	
	/** Der absolute Pfad auf die Datei */
	File file;
	
	/** Der relative Pfad (zum Repository-Root) der Datei */
	String locationInRepository;
	
	RepositoryFile(File file, String locationInRepository) {
		this.file = file;
		this.locationInRepository = locationInRepository;
		
		// Ziel-Verzeichnis erstellen
		file.getParentFile().mkdirs();
		
		// (Leere) Datei anlegen
		file.createNewFile();
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
	 * @param newFile Neuer (absoluter) Pfad der Datei
	 * @param newLocationInRepository Neuer (relativer) Pfad im Repository
	 */	
	def moveTo(File newFile, String newLocationInRepository) {

		// Verzeichnisse anlegen
		newFile.getParentFile().mkdirs();
		
		// Datei verschieben und ggf umbenennen
		boolean fileMoved = file.renameTo(newFile)
		assert fileMoved

		this.locationInRepository = newLocationInRepository;
		this.file = newFile;
	}
	
}
