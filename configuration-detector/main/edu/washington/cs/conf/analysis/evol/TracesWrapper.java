package edu.washington.cs.conf.analysis.evol;

import edu.washington.cs.conf.util.Utils;

//a wrapping class of all traces needed for error diagnosis
public class TracesWrapper {

	public final String oldSigFile;
	public final String newSigFile;
	public final String oldPredicateFile;
	public final String newPredicateFile;
	public final String oldHistoryFile;
	public final String newHistoryFile;
	
	public TracesWrapper(String oldSigFile, String newSigFile,
			String oldPredicateFile, String newPredicateFile,
			String oldHistoryFile, String newHistoryFile) {
		Utils.checkFileExistence(oldSigFile);
		Utils.checkFileExistence(newSigFile);
		Utils.checkFileExistence(oldPredicateFile);
		Utils.checkFileExistence(newPredicateFile);
		Utils.checkFileExistence(oldHistoryFile);
		Utils.checkFileExistence(newHistoryFile);
		this.oldSigFile = oldSigFile;
		this.newSigFile = newSigFile;
		this.oldPredicateFile = oldPredicateFile;
		this.newPredicateFile = newPredicateFile;
		this.oldHistoryFile = oldHistoryFile;
		this.newHistoryFile = newHistoryFile;
	}
	
}
