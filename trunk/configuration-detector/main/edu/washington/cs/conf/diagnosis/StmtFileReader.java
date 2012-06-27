package edu.washington.cs.conf.diagnosis;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Files;

public class StmtFileReader {

	private final String fileName;
	
	public StmtFileReader(String fileName) {
		this.fileName = fileName;
	}
	
	public List<StmtExecuted> readStmts() {
		List<StmtExecuted> retStmts = new LinkedList<StmtExecuted>();
		
		List<String> content = Files.readWholeNoExp(fileName);
		for(String line : content) {
			if(line.trim().isEmpty()) {
				continue;
			}
			try {
			    StmtExecuted stmt = new StmtExecuted(line);
			    retStmts.add(stmt);
			} catch (RuntimeException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return retStmts;
	}
	
	public static List<StmtExecuted> readStmts(String fileName) {
		return new StmtFileReader(fileName).readStmts();
	}
	
}