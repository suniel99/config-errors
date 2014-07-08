package edu.washington.cs.conf.mutation;

import java.util.Collection;

import edu.washington.cs.conf.util.Utils;

public class DetectionWorkflow {
	
	private ProgramRunner runner = null;
	
	private ReportGenerator reporter = new ReportGenerator();
	
	private UserManual manual = null;
	
	public DetectionWorkflow(Class<? extends ProgramRunner> clz) {
		try {
			runner = clz.newInstance();
		} catch (Exception e) {
			System.out.println("Cannot instantiate class: " + clz);
			e.printStackTrace();
		}
	}
	
	public void setUserManual(UserManual manual) {
		Utils.checkNotNull(manual);
		this.manual = manual;
	}

	public void detect() {
		//the basic workflow
		runner.setUpEnv();
		
		//collect the results
		Collection<ExecResult> execResults = runner.execute();
		for(ExecResult result : execResults) {
			//analyze it and generate the report
			if(result.pass()) {
				continue; //do nothing
			}
			//get the config option and message
			String option = result.getMutatedOption();
			String message = result.getMessage();
			//check the adequancy of the error message
			MessageAdequacy adequancy = MessageAnalyzer.isMessageAdequate(option, message, manual);
			if(!adequancy.isAdequate()) {
				//generate a report
				reporter.addToReport(adequancy, result);
			}
		}
		
		runner.clearEnv();
	}
	
	public ReportGenerator getReport() {
		return this.reporter;
	}
	
}
