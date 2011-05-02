package com.ql.util.express.test;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class ATempTest {
	@Test
	public void test2Java() throws Exception {
		String express = " 3 + 4 + 5 + 3";
		ExpressRunner runner = new ExpressRunner();		
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Object r =   runner.execute(express, context, null, false,true);
		System.out.println(r);	
	}
	
	
	//@Test
	public void testLoadFromFile() throws Exception{	
		ExpressRunner runner = new ExpressRunner(true,true);
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("Test");
		BeanExample bean = new BeanExample();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();	
		context.put("bean",bean);
		Object r = runner.execute(new InstructionSet[]{
				loader.getInstructionSet("Test")
		}, loader, context, null, false,false,null);
		System.out.println(r );
		System.out.println(context);
	}	
}
