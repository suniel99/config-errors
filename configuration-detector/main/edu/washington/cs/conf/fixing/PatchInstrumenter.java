package edu.washington.cs.conf.fixing;

import java.io.Writer;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.util.Utils;

/**
 * Patching the original program via instrumentation
 * */
public class PatchInstrumenter extends AbstractInstrumenter {

	private final FixingPlan plan;
	
	public PatchInstrumenter(FixingPlan plan) {
		Utils.checkNotNull(plan);
		this.plan = plan;
	}
	
	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
