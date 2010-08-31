package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class IfTest {
//18658122779
	public static void main(String[] args) throws Exception {
	   new IfTest().testDemo();

		long s = System.currentTimeMillis();
		int count = 10000;
		for(int i=0;i<count;i++){
			 f();
		}
		System.out.println("÷¥––∫ƒ ±£∫" + (System.currentTimeMillis() - s));
		
	}
	public static int f(){
		int qh = 0;
		for (int i = 1; i <= 10; i = i + 1) {
			if (i > 5) {
				break;
			}
			for (int j = 0; j < 10; j = j + 1) {
				if (j > 5) {
					break;
				}
				;
				qh = qh + j;
			}
		}
		
		return qh;
	}
	@org.junit.Test
	public  void testDemo() throws Exception{
		long s = System.currentTimeMillis();
		String expressString ="qh = 0; —≠ª∑(def int i = 1;  i<=10;i = i + 1){ if(i > 5) then{ ÷’÷π;}; " +
				"—≠ª∑(def int j=0;j<10;j= j+1){  " +
				"    if(j > 5)then{" +
				"       ÷’÷π;" +
				"    }; " +
				"    qh = qh + j;" +
				//"   ¥Ú”°(i +\":\" + j+ \":\" +qh);"+
				" };  " +
				"};" +
				"return qh;";
		System.out.println(expressString);
		ExpressRunner runner = new ExpressRunner();		
		runner.addOperatorWithAlias("—≠ª∑", "for",null);
		runner.addOperatorWithAlias("ºÃ–¯", "continue",null);
		runner.addOperatorWithAlias("÷’÷π", "break",null);
		runner.addFunctionOfServiceMethod("¥Ú”°", System.out, "println", new String[]{Object.class.getName()}, null);
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();		
		context.put("bean", new BeanExample("qhlhl2010@gmail.com"));
		context.put("name","xuannn");		
		int count = 10000;
		Object r = null;
		s = System.currentTimeMillis();
		r = runner.execute(expressString, null, true, context,null,false);

		System.out.println("±‡“Î∫ƒ ±£∫" + (System.currentTimeMillis() - s));
		
		for(int i=0;i<count;i++){
			r = runner.execute(expressString, null, true, context,null,false);
			//System.out.println(i + ":" + r);
		}
		System.out.println("÷¥––∫ƒ ±£∫" + (System.currentTimeMillis() - s));
		
		System.out.println(context);	
	}
}
