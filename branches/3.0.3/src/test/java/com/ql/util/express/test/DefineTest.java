package com.ql.util.express.test;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class DefineTest {
	@org.junit.Test
	public void testDefExpressInner() throws Exception{
		String express = "int qh =  1 ";
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		ExpressRunner runner = new ExpressRunner(false,true);	
		context.put("qh",100);
		Object r = runner.execute(express,context, null, false,false);
		Assert.assertTrue("���ʽ�������������", r.toString().equalsIgnoreCase("1"));
		Assert.assertTrue("���ʽ�������������", context.get("qh").toString().equalsIgnoreCase("100"));
	}		
	
	@org.junit.Test
	public void testDefUserContext() throws Exception{
		String express = "qh = 1 + 1";
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		ExpressRunner runner = new ExpressRunner();
		context.put("qh",100);
		Object r = runner.execute(express,context, null, false,false);
		Assert.assertTrue("���ʽ�������������", r.toString().equalsIgnoreCase("2"));
		Assert.assertTrue("���ʽ�������������", context.get("qh").toString().equalsIgnoreCase("2"));
	}
	@org.junit.Test
	public void testAlias() throws Exception {
		String express = " ������� qh example.child; "
				+ "{������� qh example.child.a;" + " qh =qh + \"-ssss\";" + "};"
				+ " qh.a = qh.a +\"-qh\";" + " return example.child.a";
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("�������", "alias", null);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample());
		runner.addOperatorWithAlias("���", "if", null);
		runner.addOperatorWithAlias("��", "then", null);
		runner.addOperatorWithAlias("����", "else", null);
		Object r = runner.execute(express,context, null, false,false);
		Assert.assertTrue("����ʵ�� ����", r.toString().equalsIgnoreCase("qh-ssss-qh"));
		Assert.assertTrue("����ʵ�� ����", ((BeanExample) context.get("example")).child.a.toString().equalsIgnoreCase("qh-ssss-qh"));
	}
	@org.junit.Test
	public void testMacro() throws Exception{
		String express ="�����  �ͷ�   {bean.unionName(name)}; �ͷ�; return  �ͷ�";
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("�����", "macro", null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");
		Object r = runner.execute(express,context, null, false,false);
		Assert.assertTrue("������ ����", r.toString().equalsIgnoreCase("qhlhl2010@gmail.com-xuannn"));
		System.out.println(r);
	}	
	@Test
	public void test_�Զ��庯��() throws Exception{		
		String express ="���庯��  �ݹ�(int a){" +
				" if(a == 1)then{ " +
				"   return 1;" +
				"  }else{ " +
				"     return �ݹ�(a - 1) *  a;" +
				"  } " +
				"}; " +
				"�ݹ�(10);";
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("���庯��", "function",null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		Object r = runner.execute(express,context, null, true,false);
		Assert.assertTrue("�Զ��庯�� ����", r.toString().equals("3628800"));
	}	
	@org.junit.Test
	public void testProperty() throws Exception{
		//String express =" cache isVIP(\"qh\") ;  cache isVIP(\"xuannan\") cache isVIP(\"qh\") ;";
		
		String express =" example.child.a = \"ssssssss\";" +
				" map.name =\"ffff\";" +
				"return map.name;";
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample("����"));
		context.put("map",new HashMap<String,Object>());
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1����VIP�û�");
		Object r = runner.execute(express,context, null, false,false);
		Assert.assertTrue("���Բ�������", r.toString().equalsIgnoreCase("ffff"));
		Assert.assertTrue("���Բ�������", ((BeanExample)context.get("example")).child.a.toString().equalsIgnoreCase("ssssssss"));		
	}	
	@org.junit.Test
	public void test����ִ��ָ��() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("�����", "macro", null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");
		InstructionSet[] sets =  new InstructionSet[]{
				runner.parseInstructionSet("int qh = 1;"),
				runner.parseInstructionSet("qh = qh + 10;"),
				runner.parseInstructionSet("�����  �ͷ�   {qh = qh + 100 };"),
				runner.parseInstructionSet("�ͷ�;"),
				runner.parseInstructionSet("qh = qh + 1000;"),
		};
		Object r = runner.execute(sets, context, null, true,false,null);
//		 public Object execute(InstructionSet[] instructionSets,ExpressLoader loader,IExpressContext context,
//				  List errorList,FuncitonCacheManager aFunctionCacheMananger,boolean isTrace,boolean isCatchException,
//					Log aLog);
		Assert.assertTrue("����ʵ�� ����", r.toString().equalsIgnoreCase("1111"));
//		System.out.println(r);
//		System.out.println(context);
	}	
	@org.junit.Test
	public void test���������ű�() throws Exception{
		ExpressRunner runner = new ExpressRunner();
		runner.addOperatorWithAlias("�����", "macro", null);
		runner.loadMutilExpress("����", "int qh = 100;");
		runner.loadMutilExpress("�ۼ�", "qh = qh + 100;");
		runner.loadMutilExpress("ִ��", "�ۼ�;�ۼ�;");
		runner.loadMutilExpress("����", "return qh;");
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");
		Object r = runner.executeByExpressName(new String[]{
				"����","ִ��","ִ��","����"			
		}, context, null,  true,false,null);
		
		System.out.println(r);
		Assert.assertTrue("����ʵ�� ����", r.toString().equalsIgnoreCase("500"));
	
	}	
	@org.junit.Test
	public  void test_ѭ��() throws Exception{
		long s = System.currentTimeMillis();
		String express ="qh = 0; ѭ��(int i = 1;  i<=10;i = i + 1){ if(i > 5) then{ ��ֹ;}; " +
				"ѭ��(int j=0;j<10;j= j+1){  " +
				"    if(j > 5)then{" +
				"       ��ֹ;" +
				"    }; " +
				"    qh = qh + j;" +
				//"   ��ӡ(i +\":\" + j+ \":\" +qh);"+
				" };  " +
				"};" +
				"return qh;";
		ExpressRunner runner = new ExpressRunner();		
		runner.addOperatorWithAlias("ѭ��", "for",null);
		runner.addOperatorWithAlias("����", "continue",null);
		runner.addOperatorWithAlias("��ֹ", "break",null);
		runner.addFunctionOfServiceMethod("��ӡ", System.out, "println", new String[]{Object.class.getName()}, null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");		
		int count = 1;
		s = System.currentTimeMillis();
		Object r = runner.execute(express,context, null, false,false);

		System.out.println("�����ʱ��" + (System.currentTimeMillis() - s));
		
		for(int i=0;i<count;i++){
			r = runner.execute(express,context, null, false,false);
			Assert.assertTrue("ѭ���������", r.toString().equals("75"));
		}
		System.out.println("ִ�к�ʱ��" + (System.currentTimeMillis() - s));		
		System.out.println(context);	
	}	
}
