package com.ql.util.express.test;

import java.math.BigDecimal;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class ATempTest {
	public static void main(String[] args) throws Exception {

		double s = 0;
		for (int i = 0; i < 26; i++)
			s += 0.1;
		System.out.println(s);
		BigDecimal bs = new BigDecimal("0");
		for (int i = 0; i < 26; i++)
			bs = bs.add(new BigDecimal("11"));
		System.out.println(bs);
		System.out.println("小数位数：" + bs.scale());
		System.out.println("总长度：" + bs.precision());
		
	}
	
	
	@Test
	public void testLoadFromFile() throws Exception{	
		ExpressRunner runner = new ExpressRunner(true,true);
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("Test");
		BeanExample bean = new BeanExample();
		DefaultContext<String, Object>  context = new DefaultContext<String, Object>();	
		context.put("bean",bean);
		Object r = runner.execute(new InstructionSet[]{
				loader.getInstructionSet("Test")
		}, loader, context, null, null, false,false,null);
		System.out.println(r );
		System.out.println(context);
	}	
}
