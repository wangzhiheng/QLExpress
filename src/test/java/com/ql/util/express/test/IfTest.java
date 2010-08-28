package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class IfTest extends ExpressRunner {
	@org.junit.Test
	public void testDemo() throws Exception{
		String expressString ="qh = 100; for{def int i = 1;  i<=10;i = i + 1; }{qh = qh + 10;}";
		ExpressRunner runner = new ExpressRunner();		
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");		
		Object r = runner.execute(expressString, null, false, context);
		System.out.println(r);
		System.out.println(context);
	
	}
}
