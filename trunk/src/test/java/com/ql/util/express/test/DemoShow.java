package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;

public class DemoShow {

	/**
	 * ��������
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testArithmetic() throws Exception {
		ExpressRunner runner = new ExpressRunner(true, true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		runner.execute("1+2*3", context, null, false, true);
	}

	/**
	 * forѭ��
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testForLoop() throws Exception {
		ExpressRunner runner = new ExpressRunner(true, true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		runner.execute("sum=0;for(i=0;i<10;i=i+1){sum=sum+i;}", context, null,
				false, true);
	}

	/**
	 * forǶ��ѭ��
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testForLoop2() throws Exception {
		ExpressRunner runner = new ExpressRunner(true, true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		runner.execute(
				"sum=0;for(i=0;i<10;i=i+1){for(j=0;j<10;j++){sum=sum+i+j;}}",
				context, null, false, true);
	}

	/**
	 * ��ŵ���㷨
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testHanoiMethod() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		runner.addFunctionOfClassMethod("��ŵ���㷨", DemoShow.class.getName(),
				"hanoi", new Class[] { int.class, char.class, char.class,
						char.class }, null);
		runner.execute("��ŵ���㷨(3, '1', '2', '3')", null, null, false, false);
	}

	/**
	 * ��ŵ���㷨2
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testHanoiMethod2() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		runner.addFunctionOfServiceMethod("��ŵ���㷨", new DemoShow(), "hanoi",
				new Class[] { int.class, char.class, char.class, char.class },
				null);
		runner.execute("��ŵ���㷨(3, '1', '2', '3')", null, null, false, false);
	}
	
	/**
	 * ��ŵ���㷨3
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testHanoiMethod3() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		runner.addFunctionOfServiceMethod("��ŵ���㷨", new DemoShow(), "hanoi",
				new Class[] { int.class, char.class, char.class, char.class },
				null);
		runner.addMacro("��ŵ���㷨��ʾ", "��ŵ���㷨(3, '1', '2', '3')");
		runner.execute("��ŵ���㷨��ʾ", null, null, false, false);
	}
	// ��n���̴�one������two��,�Ƶ�three��
	public void hanoi(int n, char one, char two, char three) {
		if (n == 1)
			move(one, three);
		else {
			hanoi(n - 1, one, three, two);
			move(one, three);
			hanoi(n - 1, two, one, three);
		}
	}
	private void move(char x, char y) {
		System.out.println(x + "--->" + y);
	}
	
	/**
	 * �Զ��������
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testOperator() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		runner.addOperator("join",new JoinOperator());
		Object r = runner.execute("1 join 2 join 3", context, null, false, false);
		System.out.println(r);
	}	
	@SuppressWarnings({ "unchecked", "rawtypes","serial" })
	public class JoinOperator extends Operator{
		public Object executeInner(Object[] list) throws Exception {
			Object opdata1 = list[0];
			Object opdata2 = list[1];
			if(opdata1 instanceof java.util.List){
				((java.util.List)opdata1).add(opdata2);
				return opdata1;
			}else{
				java.util.List result = new java.util.ArrayList();
				result.add(opdata1);
				result.add(opdata2);
				return result;				
			}
		}
	}
	
	/**
	 * �滻������
	 * 
	 * @throws Exception
	 */
	@org.junit.Test
	public void testReplaceOperator() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Object r = runner.execute("1 + 2 + 3", context, null, false, false);
		System.out.println(r);
		runner.replaceOperator("+",new JoinOperator());
		r = runner.execute("1 + 2 + 3", context, null, false, false);
		System.out.println(r);
	}
	
	/**
	 * Ԥ���ر��ʽ & ������
	 * @throws Exception
	 */
	@org.junit.Test
	public void testVirtualClass() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, true);
		runner.loadMutilExpress("���ʼ��", "class People(){sex;height;money;skin};");
		runner.loadMutilExpress("����Сǿ", "a = new People();a.sex='male';a.height=185;a.money=10000000;a.skin='white';return a;");
		runner.loadMutilExpress("���", "if(a.sex=='male' && a.height>180 && a.money>5000000) return '�߸�˧���������'");
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		
		Object r = runner.execute("���ʼ��;����Сǿ;���", context, null, false, true);
		System.out.println(r);
	}

}
