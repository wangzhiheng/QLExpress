package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;

public class OpCallTest {
		@Test
		public void testList() throws Exception {
			ExpressRunner runner = new ExpressRunner(false, true);
			runner.addOperator("@love", new LoveOperator("@love"));	
			runner.loadMutilExpress(null, "function abc(String s){println(s)}");
			String[][] expressTest = new String[][] {
					{"+ 1 2","3"},
					{"@love 'a' 'b'","b{a}b"},
					{"println \"ssssss\"","null"},
					{"println (\"ssssss\")","null"},
					{"abc (\"bbbbbbbb\")","null"},
					};
			IExpressContext<String, Object> expressContext = new ExpressContextExample(
					null);

			for (int point = 0; point < expressTest.length; point++) {
				String expressStr = expressTest[point][0];
				List<String> errorList = new ArrayList<String>();
				Object result = runner.execute(expressStr, expressContext, null,
						false, true);
				if (result == null
						&& expressTest[point][1].equalsIgnoreCase("null") == false
						|| expressTest[point][1].equalsIgnoreCase("null")
						&& result != null
						|| result != null
						&& expressTest[point][1]
								.equalsIgnoreCase(result.toString()) == false) {
					throw new Exception("�������,��������Ԥ�ڵĲ�ƥ��:" + expressStr + " = "
							+ result + "��������ֵ�ǣ�" + expressTest[point][1]);
				}
				System.out.println("Example " + point + " : " + expressStr + " =  "
						+ result);
				if (errorList.size() > 0) {
					System.out.println("\t\tϵͳ����Ĵ�����ʾ��Ϣ:" + errorList);
				}
			}
		}
}
class PrintOperator extends Operator {	
	public PrintOperator(String aName) {
		this.name= aName;
	}
	public Object executeInner(Object[] list)
			throws Exception {
		System.out.println(list[0]);
		return null;
	}
}