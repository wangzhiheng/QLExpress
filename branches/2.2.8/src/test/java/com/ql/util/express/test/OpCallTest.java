package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class OpCallTest {
		@Test
		public void testList() throws Exception {
			ExpressRunner runner = new ExpressRunner(false, true);
			runner.addOperator("@love", new LoveOperator("@love"));	
			runner.loadMutilExpress(null, "function abc(String s){println(s)}");
			runner.addOperatorWithAlias("��ӡ","println",null);
			runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
					"isVIP", new Class[]{String.class},"");
			runner.addOperatorWithAlias("�Ƿ�VIP","isVIP","�װ���$1,�㻹����VIP�û�");
			
			String[][] expressTest = new String[][] {
					{"+ 1 2","3"},
					{"@love 'a' 'b'","b{a}b"},
					{"println \"ssssss\"","null"},
					{"println (\"ssssss\")","null"},
					{"abc (\"bbbbbbbb\")","null"},
					{"��ӡ (\"������������\")","null"},
					{"isVIP (\"����\")","false"},
					{"�Ƿ�VIP (\"����\")","false"},
					};
			IExpressContext<String, Object> expressContext = new ExpressContextExample(
					null);

			for (int point = 0; point < expressTest.length; point++) {
				String expressStr = expressTest[point][0];
				List<String> errorList = new ArrayList<String>();
				Object result = runner.execute(expressStr, expressContext, errorList,
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