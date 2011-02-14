package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class ArrayTest {

	@Test
	public void testABC() throws Exception {
		String express = "int[][] abc = new int[2][2];" +
				" int[] b = new int[2]; " +
				"abc[0] = b;" +
				" b[0] =11;" +
				" abc[0][1] = 22; " +
				"return abc;";
		ExpressRunner runner = new ExpressRunner(true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		int[][] r = (int[][])runner.execute(express, context, null, false, true);
		System.out.println(r[0][1]);
		Assert.assertTrue("数组操作实现错误",r[0][1]== 22);
	}
}
