package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class IfTest {
	@Test
	public void testIf() throws Exception{
		String[][] expresses = new String[][]{			
				{"if 1==1 then return 100   else return 10;","100"},
				{"if 1==2 then return 100   else return 10;","10"},
				{"if 1==1 then return 100;  else return 10;","100"},
				{"if 1==2 then return 100;  else return 10;","10"},
				{"if 1==1 then {return 100}  else {return 10;}","100"},
				{"if 1==2 then {return 100}  else {return 10;}","10"},
				{"if 1==1 then return 100 ; return 10000;","100"},
				{"if 1==2 then return 100; return 10000;","10000"},
				{"if (1==1)  return 100   else return 10;","100"},
				{"if (1==2)  return 100   else return 10;","10"},
				{"if (1==1)  return 100;  else return 10;","100"},
				{"if (1==2)  return 100;  else return 10;","10"},
				{"if (1==1)  {return 100}  else {return 10;}","100"},
				{"if (1==2)  {return 100}  else {return 10;}","10"},
				{"if (1==1) return 100 ; return 10000;","100"},
				{"if (1==2) return 100; return 10000;","10000"},				
		};
		for(int i=0;i<expresses.length;i++){
			IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
			ExpressRunner runner = new ExpressRunner(true);
			runner.addOperatorWithAlias("��", "+",null);
			runner.addOperator("love","+",new LoveOperator("love"));
			Object result = runner.execute(expresses[i][0],expressContext, null, false,true);
			System.out.println("��������" + result);
			System.out.println("���������" + expressContext);		
			Assert.assertTrue("���ʽִ�д���:" + expresses[i][0] + " ����ֵ��" + expresses[i][1] +" ��������" + result ,expresses[i][1].equals(result == null?"null":result.toString()));
		}
	}	
}
