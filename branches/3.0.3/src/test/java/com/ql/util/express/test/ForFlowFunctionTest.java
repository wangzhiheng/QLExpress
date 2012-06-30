package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;


public class ForFlowFunctionTest {

	@Test
	public void testABC() throws Exception {
		String express = "for(i=0;i<1;i=i+1){" + "��ӡ(70)"
				+ "}��ӡ(70); return 10";
		ExpressRunner runner = new ExpressRunner(false,true);
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println",
				new String[] { "int" }, null);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Object r = runner.execute(express, context, null, false, true);
		Assert.assertTrue("forѭ���������һ��������ʱ�����", r.toString().equals("10"));
	}

}
