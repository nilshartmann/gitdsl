import gitdsl.GitRepository;

GitRepository.recreateAt("/tmp/testrepo").setup {

	// Eine Datei mit Inhalt anlegen
	addFile 'f1', path: 'src/main/java/hello/World.java', content: '''
Zeile1, Zeile2, Zeile3
'''
	
	// Eine Datei mit Default-Inhalt anlegen
	addFile 'f2', path: 'src/main/java/hello/Welt.java'
	
	// Commit mit Kommentar
	commit 'Zwei Dateien hinzugefügt'
	
	// Datei ohne Id anlegen (mit Default-Inhalt)
	addFile path: 'src/main/java/hello/Monde.java'
	
	// Commit ohne Kommentar (generierter Kommentar)
	commit()
	
	
	
}

