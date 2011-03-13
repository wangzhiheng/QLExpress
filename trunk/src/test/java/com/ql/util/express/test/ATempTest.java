package com.ql.util.express.test;

import org.apache.commons.logging.Log;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class ATempTest {
	@Test
	public void testLoadFromFile() throws Exception{	
		ExpressRunner runner = new ExpressRunner(true);
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("Test");
		
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		Object r = runner.execute(new InstructionSet[]{
				loader.getInstructionSet("Test")
		}, loader, context, null, null, true,false,null);
		System.out.println(r);
		System.out.println(context);
	}	
}
