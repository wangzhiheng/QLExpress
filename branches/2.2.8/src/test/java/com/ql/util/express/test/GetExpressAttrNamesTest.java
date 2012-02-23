package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.ExpressRunner;

public class GetExpressAttrNamesTest {

	@Test
	public void testABC() throws Exception {
		String express = "alias qh 100; exportAlias fff qh; int a = b; c = a;macro  �ͷ�    {100 + 100} �ͷ�; qh ;fff;";
		ExpressRunner runner = new ExpressRunner(true,true);
		String[] names = runner.getOutVarNames(express);
		for(String s:names){
			System.out.println("var : " + s);
		}
		Assert.assertTrue("��ȡ�ⲿ���Դ���",names.length == 2);
		Assert.assertTrue("��ȡ�ⲿ���Դ���",names[0].equalsIgnoreCase("b"));
		Assert.assertTrue("��ȡ�ⲿ���Դ���",names[1].equalsIgnoreCase("c"));
	}
}
