package com.ql.util.express.test;

import com.ql.util.express.ExpressRunner;

public class FunctionTest {
	@org.junit.Test
	public void testDemo() throws Exception{
		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
		ExpressRunner runner = new ExpressRunner();
		Object result = runner.execute(express, null, false, null);
		System.out.println("表达式计算：" + express + " = " + result);
	}
}
