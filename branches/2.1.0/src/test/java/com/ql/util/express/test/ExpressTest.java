package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class ExpressTest {

	@org.junit.Test
	public void testDemo() throws Exception{
		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
		ExpressRunner runner = new ExpressRunner();
		Object r = runner.execute(express,null, null, false,false);
		Assert.assertTrue("���ʽ����", r.toString().equalsIgnoreCase("117"));
		System.out.println("���ʽ���㣺" + express + " = " + r);
	}

	@org.junit.Test
	public void tes10000��() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
		int num = 100000;
		runner.execute(express,null, null, 	true,false);
		long start = System.currentTimeMillis();
		for(int i = 0;i< num;i++){
			runner.execute(express,null, null, true,false);
		}
		System.out.println("ִ��" + num +"��\""+ express +"\" ��ʱ��"
				+ (System.currentTimeMillis() - start));
	}

	@org.junit.Test
	public void testExpress() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		
		runner.addOperatorWithAlias("���", "if",null);
		runner.addOperatorWithAlias("��", "then",null);
		runner.addOperatorWithAlias("����", "else",null);

		runner.addOperator("love", new LoveOperator("love"));
		runner.addOperatorWithAlias("����", "in", "�û�$1��������ķ�Χ");
		runner.addOperatorWithAlias("myand", "and", "�û�$1��������ķ�Χ");
		runner.addFunction("�ۼ�", new GroupOperator("�ۼ�"));
		runner.addFunction("group", new GroupOperator("group"));
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1����VIP�û�");
		runner.addFunctionOfClassMethod("ȡ����ֵ", Math.class.getName(), "abs",
				new String[] { "double" }, null);
		runner.addFunctionOfClassMethod("ȡ����ֵTWO", Math.class.getName(), "abs",
				new Class[] { double.class }, null);
		runner.addFunctionOfClassMethod("ת��Ϊ��д", BeanExample.class.getName(),
				"upper", new String[] { "String" }, null);		
		runner.addFunctionOfClassMethod("testLong", BeanExample.class.getName(),
				"testLong", new String[] { "long" }, null);		
		String[][] expressTest = new String[][] {
				{ "isVIP(\"qh\") ; isVIP(\"xuannan\"); return isVIP(\"qh\") ;", "false" },				
				{ "���  ��������  ��  'a' love 'b'  ����   'b' love 'd' ", "b{a}b" },
				{"int defVar = 100; defVar = defVar + 100;", "200"},
				{"int a=0; if false then a = 5 else  a=10+1 ; return a ","11"},
				{ " 3+ (��� 1==2 �� 4 ���� 3) +8","14"},
				{ " ���  (��� 1==2 �� false ���� true) �� {2+2;} ���� {20 + 20;} ","4"},
				
				{ "System.out.println(\"ss\")", "null" },
				{"unionName = new com.ql.util.express.test.BeanExample(\"����\").unionName(\"����\")",
						"����-����" }, 
						{ "group(2,3,4)", "9" },
						{ "ȡ����ֵ(-5.0)", "5.0" },
						{ "ȡ����ֵTWO(-10.0)", "10.0" },
				{ "max(2,3,4,10)", "10" },
				{ "max(3,2) + ת��Ϊ��д(\"abc\")", "3ABC" },
				{ "c = 1000 + 2000", "3000" },
				{ "b = �ۼ�(1,2,3)+�ۼ�(4,5,6)", "21" },
				{ "�������� and �����û� ", "true" },
				{ "new String(\"12345\").length()" ,"5"},
				{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" },
				{ "10 * (10 + 1) + 2 * (3 + 5) * 2", "142" },
				{ "( 2  ���� (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false" },
				{" 1!=1 and isVIP(\"qhlhl2010@gmail.com\")","false"},
				{" 1==1 or isVIP(\"qhlhl2010@gmail.com\") ","true"},
				{ "abc == 1", "true" },
				{ "testLong(abc)", "toString-long:1" },
				{ "bean.testLongObject(abc)", "toString-LongObject:1" },
				
				{"sum=0;n=7.3;for(i=0;i<n;i=i+1){sum=sum+i;};sum;","28"}
				};
		IExpressContext<String,Object> expressContext = new ExpressContextExample(null);
		expressContext.put("b", new Integer(200));
		expressContext.put("c", new Integer(300));
		expressContext.put("d", new Integer(400));
		expressContext.put("bean", new BeanExample());
		expressContext.put("abc",1l);
		expressContext.put("defVar",1000);
		
		
		for (int point = 0; point < expressTest.length; point++) {
			String expressStr = expressTest[point][0];
			List<String> errorList = new ArrayList<String>();
			Object result = runner.execute(expressStr,expressContext, null, false,true);
			if (expressTest[point][1].equalsIgnoreCase("null")
					&& result != null
					|| result != null
					&& expressTest[point][1].equalsIgnoreCase(result							
							.toString()) == false) {
				throw new Exception("�������,��������Ԥ�ڵĲ�ƥ��:" + expressStr + " = " + result + "��������ֵ�ǣ�" + expressTest[point][1]);
			}
			System.out.println("Example " + point + " : " + expressStr + " =  " + result);
			if(errorList.size() > 0){
			   System.out.println("\t\tϵͳ����Ĵ�����ʾ��Ϣ:" + errorList);
			}
		}
		System.out.println(expressContext);
	 }
}
