package com.ql.util.express.test;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;

public class ATempTest {
	@Test
	public void test2Java() throws Exception {
		String express = "macro ÐþÄÑ { abc(100);}   ÐþÄÑ;";
		ExpressRunner runner = new ExpressRunner(false,true);	
		runner.addFunctionOfClassMethod("abc", BeanExample.class.getName(),
				"testLong", new String[] { "long" }, null);	
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
		Object r = runner.executeByExpressName("Test", context, null, false,false,null);
		System.out.println(r );
		System.out.println(context);
	}	
}
