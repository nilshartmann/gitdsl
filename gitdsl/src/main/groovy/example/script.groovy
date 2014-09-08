package example
import gitdsl.GitRepository;

// Neues Repository in /tmp/testrepo anlegen
// ACHTUNG! Das Verzeichnis wird GELÖSCHT, wenn es schon vorhanden ist
GitRepository.recreateAt("/tmp/testrepo").setup {

	// Eine Datei mit Inhalt anlegen
	// Fehlende Pfade werden automatisch erzeugt
	// Über ersten Parameter (Id) kann die Datei später referenziert werden
	addFile 'f1', path: 'src/main/java/hello/World.java', content: '''
Zeile1
Zeile2
Zeile3
'''
	// Eine Datei mit Default-Inhalt anlegen
	addFile 'f2', path: 'src/main/java/hello/Welt.java'

	// Commit mit Kommentar
	commit 'Zwei Dateien hinzugefügt'

	// Datei ohne Id anlegen (mit Default-Inhalt)
	addFile path: 'src/main/java/hello/Monde.java'

	// Commit ohne Kommentar (generierter Kommentar)
	commit()

	// Datei-Inhalt ändern
	modifyFile 'f1', content: '''
Ein anderer Inhalt
'''
	commit "Datei f1 geändert"


	checkout "master"

	// 'orhpan' branch anlegen
	//	checkout "example2", orphan: true

	// Wechselt auf den angegebenen Branch. Falls dieser nicht exisitert,
	// wird er zuvor angelegt
	checkout "feature-1"

	// Datei in ein anderes Verzeichnis schieben
	moveFile 'f1', topath: 'src/main/java/en/hello/World.java'

	// Datei umbenennen, neuen Inhalt setzen
	moveFile 'f2', topath: 'src/main/java/hello/HalloWelt.java', content: '''
public class HalloWelt {
}
'''

	commit 'Dateien umbenannt'
	checkout "master"

	// Merge: BranchName. (Optional: Commit-Message und noff (true|false)
	merge 'feature-1', message: 'Feature-1 beendet', noff: true

	checkout 'feature-2'
	removeFile 'f1'
	commit 'Datei World.java geloescht'
			
}

