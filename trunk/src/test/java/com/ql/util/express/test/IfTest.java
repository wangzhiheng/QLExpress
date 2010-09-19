package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class IfTest {

	
	@Test
	public void testMacro() throws Exception{	
		ExpressRunner runner = new ExpressRunner();
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("functiondef");
		loader.loadExpressFromFile("main");
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		
		Object r = runner.execute(new InstructionSet[]{
				loader.getInstructionSet("main")
		}, loader, context, null, null, true);
		System.out.println(r);
		System.out.println(context);
	}	
	
}
