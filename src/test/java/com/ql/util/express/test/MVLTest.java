package com.ql.util.express.test;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.thoughtworks.xstream.XStream;

public class MVLTest {
	@org.junit.Test
	public void tes10000次() throws Exception {
		ExpressRunner runner = new ExpressRunner();
		String express = "a+10";
		IExpressContext context = new ExpressContextExample(null);
		context.put("a", 10);
		
		runner.execute(express,context, null, true,false);
		int num = 100000;
		Object result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			result = runner.execute(express,context, null, true,false);
		}
		System.out.println("执行" + num + "次\"" + express + "\" 耗时："
				+ (System.currentTimeMillis() - start) + ":" + result);
		System.out.println(context);
	}
	
	@org.junit.Test
	public void testMVL() {
		String express = "a+10";
		Map vars = new HashMap();
		vars.put("a", 10);
		Serializable compiled = MVEL.compileExpression(express);

		XStream xstream = new XStream();
		StringWriter writer = new StringWriter();
		xstream.toXML(compiled, writer);

		System.out.println(writer.getBuffer().toString());
		int num = 100000;
		Object result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			result = MVEL.executeExpression(compiled,vars);
		}
		System.out.println("执行" + num + "次\"" + express + "\" 耗时："
				+ (System.currentTimeMillis() - start) + ":" + result);
		System.out.println(vars);
		
	}

}
