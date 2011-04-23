package com.ql.util.express.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;

public class RunExample implements ApplicationContextAware, Runnable {
	private ApplicationContext applicationContext;
	ExpressRunner runner;

	public RunExample()throws Exception{
		runner = new ExpressRunner();
		initialRunner(runner);
	}
	public RunExample(ExpressRunner runner) {
		this.runner = runner;
	}
	public void setApplicationContext(ApplicationContext context) {
		this.applicationContext = context;
	}
    public static void initialRunner(ExpressRunner runner) throws Exception{
		runner.addOperator("love", new LoveOperator("love"));
		runner.addOperator("equalIn", new EqualIn());
		runner.addOperatorWithAlias("属于", "in", "用户$1不在允许的范围");
		runner.addOperatorWithAlias("myand", "and", "用户$1不在允许的范围");
		runner.addFunction("累加", new GroupOperator("累加"));
		runner.addFunction("group", new GroupOperator("group"));
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1不是VIP用户");
		runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
				new String[] { "double" }, null);
		runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
				"upper", new String[] { "String" }, null);
    }
	public static void main(String[] args) throws Exception {
    	ExpressRunner runner = new ExpressRunner(false,true);
    	initialRunner(runner);
		new RunExample(runner).run(1);
		for (int i = 0; i < 20; i++) {
			new Thread(new RunExample(runner)).start();
		}
	}
	public void run() {
		run(10000000);
	}
	public void run(int num) {
		long start = System.currentTimeMillis();
		try {
			for (int j = 0; j < num; j++) {
				String[][] expressTest = new String[][] {
						{"100+200.0","300.0"}
						};
				IExpressContext<String,Object> expressContext = new ExpressContextExample(	this.applicationContext);
				expressContext.put("a", j);
				expressContext.put("b", new Integer(200));
				expressContext.put("c", new Integer(300));
				expressContext.put("d", new Integer(400));
				expressContext.put("bean", new BeanExample());
				for (int point = 0; point < expressTest.length; point++) {
					String s = expressTest[point][0];
					// 批量处理的时候可以先预处理，会加快执行效率
					//List<String> errorList = new ArrayList<String>();
					 Object result = runner.execute(s,expressContext, null, true,false);
					if (expressTest[point][1].equalsIgnoreCase("null")
							&& result != null
							|| result != null
							&& expressTest[point][1].equalsIgnoreCase(result							
									.toString()) == false) {
						throw new Exception("处理错误,计算结果与预期的不匹配");
					}
//					System.out.println(s + " 执行结果 ： " + result);
//					System.out.println("错误信息" + errorList);
				}
			//	System.out.println(expressContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + "耗时："
				+ (System.currentTimeMillis() - start));
	}
	
	public void run2(int num) {
		long start = System.currentTimeMillis();
		try {
			for (int j = 0; j < num; j++) {
				String[][] expressTest = new String[][] {
//						{ "System.out.println(\"ss\")", "null" },
						{"unionName = new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\")",
								"张三-李四" }, 
						{ "max(2,3,4,10)", "10" },
						{ " max(3,2) + 转换为大写(\"abc\")", "3ABC" },
						{ " null == null", "true" },
						{ " c = 1000 + 2000", "3000" },
						{ "b = 累加(1,2,3)+累加(4,5,6)", "21" },
						{ "三星卖家 and 消保用户 ", "true" },
						{ " ((1 +  1) 属于 (4,3,5)) and isVIP(\"qhlhl2010@gmail.com\")", "false" },
						{ "group(2,3,4)", "9" }, { "取绝对值(-5)", "5.0" },
						{ "2 属于(3,4)", "false" },
						{ "true myand false", "false" },
						{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" },
						{ " 10 * 10 + 1 + 2 * 3 + 5 * 2", "117" },
						{" 1!=1 and 2==2 and 1 == 2","false"},
						{" 80 > \"300\"","true"}
						};
				IExpressContext<String,Object> expressContext = new ExpressContextExample(	this.applicationContext);
				expressContext.put("a", j);
				expressContext.put("b", new Integer(200));
				expressContext.put("c", new Integer(300));
				expressContext.put("d", new Integer(400));
				expressContext.put("bean", new BeanExample());
				for (int point = 0; point < expressTest.length; point++) {
					String s = expressTest[point][0];
					// 批量处理的时候可以先预处理，会加快执行效率
					//List<String> errorList = new ArrayList<String>();
					 Object result = runner.execute(s,expressContext, null, false,false);
					if (expressTest[point][1].equalsIgnoreCase("null")
							&& result != null
							|| result != null
							&& expressTest[point][1].equalsIgnoreCase(result							
									.toString()) == false) {
						throw new Exception("处理错误,计算结果与预期的不匹配");
					}
//					System.out.println(s + " 执行结果 ： " + result);
//					System.out.println("错误信息" + errorList);
				}
			//	System.out.println(expressContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + "耗时："
				+ (System.currentTimeMillis() - start));
	}
		
}
class EqualIn extends Operator{

	@Override
	public Object executeInner(Object[] list) throws Exception {
		for (int i = 0 ; i < list.length ; i ++){
			System.out.println(list[i]);
		}
		return Boolean.TRUE;
	}
	
}

