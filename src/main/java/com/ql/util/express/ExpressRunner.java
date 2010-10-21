/**
 *
 * <p>Title:Operator </p>
 * <p>Description:表达式计算抽象类 </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author 墙辉
 * @version 1.0
 */

package com.ql.util.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 表达式计算的主类
 * 
 * @author qhlhl2010@gmail.com

 * 你想知道   a love b = ? 吗 ，你想随意定义自己的操作符号吗 ？ 请使用QLExpress工具包
 * 
 * 这个表达式相对别的计算工具，优点主要体现在：
      A、不需要预先加载可能需要的所有属性值
      B、 用户可以根据业务需要自定义操作符号和函数 
      C、可以同步输出判断错误信息，有利于提高业务系统在规则判断等使用场景下的用户体验。减少业务系统相关的处理代码。
   
       主要用途：一些业务规则的组合判断，同时需要输出相关的错误信息

 * 最简单Hello范例：
 * 
 *		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
 *		ExpressRunner runner = new ExpressRunner();
 *		Object result = runner.execute(express, null, false, null);
 *		System.out.println("表达式计算：" + express + " = " + result);
 *
 * 其它范例：
 *		ExpressRunner runner = new ExpressRunner();
		runner.addOperator("love", new LoveOperator("love"));
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

      在这个计算引擎里面， 执行下述表达式结果：
 *      Example 0 : System.out.println("ss") =  null
 *      Example 1 : unionName = new com.ql.util.express.test.BeanExample("张三").unionName("李四") =  张三-李四
 *      Example 2 : group(2,3,4) =  9
 *      Example 3 : 取绝对值(-5.0) =  5.0
 *      Example 4 : max(2,3,4,10) =  10
 *      Example 5 :  max(3,2) + 转换为大写("abc") =  3ABC
 *      Example 6 :  c = 1000 + 2000 =  3000
 *      Example 7 : b = 累加(1,2,3)+累加(4,5,6) =  21
 *      Example 8 : 三星卖家 and 消保用户  =  true
 *      Example 9 : 'a' love 'b' love 'c' love 'd' =  d{c{b{a}b}c}d
 *      Example 10 :  10 * 10 + 1 + 2 * 3 + 5 * 2 =  117
 *      Example 11 :  ( (1+1) 属于 (4,3,5)) and isVIP("玄难") =  false
 *      		系统输出的错误提示信息:[用户 2 不在允许的范围,  玄难 不是VIP用户]
 *      
 * 
 * 表达式支持概述：
 * 1、基本的java语法：
   * 　　A、四则运算 : 10 * 10 + 1 + 2 * 3 + 5 * 2
 *　　B、boolean运算: 3 > 2 and 2 > 3
 *    C、创建对象，对象方法调用，静态方法调用:new com.ql.util.express.test.BeanExample("张三").unionName("李四")
 *    D、变量赋值：a = 3 + 5
 *    F、支持 in,max,min:  (a in (1,2,4)) and (b in("abc","bcd","efg"))
 * 2、提供表达式上下文，属性的值不需要在初始的时候全部加入，
 *    而是在表达式计算的时候，需要什么信息才通过上下文接口获取 
 *    避免因为不知道计算的需求，而在上下文中把可能需要的数据都加入。
 *    runner.execute("三星卖家 and 消保用户",errorList,true,expressContext)
 *    "三星卖家"和"消保用户"的属性是在需要的时候通过接口去获取。
 * 3、可以将计算结果直接存储到上下文中供后续业务使用。例如：
 *       runner.execute("c = 1000 + 2000",errorList,true,expressContext);
 *       则在expressContext中会增加一个属性c=3000，也可以在expressContext实现直接的数据库操作等。
 * 4、 可以将类和Spring对象的方法映射为表达式计算中的别名，方便其他业务人员的立即和配置。例如：
 *    将 Math.abs() 映射为 "取绝对值"。 则  "取绝对值(-5.0)" = "5.0"
 *    runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",new String[] { "double" }, null);
 * 5、可以为已经存在的boolean运算操作符号设置别名，增加错误信息同步输出，在计算结果为false的时候，同时返回错误信息。例如：
 *    runner.addOperatorWithAlias("属于", "in", "用户$1不在允许的范围")。
 *    用户自定义的函数同样也可以设置错误信息：例如：
	  runner.addFunctionOfClassMethod("isOk", BeanExample.class.getName(),"isOk", new String[] { "String" }, "$1 不是VIP用户");
	 
 *    则在调用List errorList = new ArrayList();
 *			 Object result =runner.execute("( (1+1) 属于 (4,3,5)) and isOk("玄难")",errorList,true,null);
	     执行结果 result = false.同时在errorList中还会返回2个错误原因：
	      1、"用户 2 不在允许的范围"，2、玄难 不是VIP用户	  
	    这在业务系统需要进行规则计算，同时需要返回
			 
 * 6、可以自定义计算函数。例如：
 *    自定一个操作函数 group：
 *    class GroupOperator extends Operator {
	  public GroupOperator(String aName) {
		this.name= aName;
	   }
	  public OperateData executeInner(IExpressContext context, OperateData[] list) throws Exception {
		Object result = new Integer(0);
		for (int i = 0; i < list.length; i++) {
			result = OperatorOfNumber.Add.execute(result, list[i]
					.getObject(context));
		}
		return new OperateData(result, result.getClass());
	  }
     }
          然后增加到运算引擎：
     runner.addFunction("累加", new GroupOperator("累加"));
	 runner.addFunction("group", new GroupOperator("group"));
          则 执行：group(2,3,4)  = 9 ,累加(1,2,3)+累加(4,5,6)=21
 * 7、可以自定义新的操作符号  。自定义的操作符号优先级设置为最高。例如 ：
 *   自定一个操作函数 love：
 *   class LoveOperator extends Operator {	
	public LoveOperator(String aName) {
		this.name= aName;
	}
	public OperateData executeInner(IExpressContext context, OperateData[] list)
			throws Exception {
		String op1 = list[0].getObject(context).toString();
		String op2 = list[1].getObject(context).toString();
		String result = op2 +"{" + op1 + "}" + op2;		
		return new OperateData(result, result.getClass());
	}
   }
         然后增加到运算引擎：
      runner.addOperator("love", new LoveOperator("love"));
        则 执行：'a' love 'b' love 'c' love 'd' = "d{c{b{a}b}c}d"
        
  8、运算引擎在没有预编译的情况下， 执行10万次  "10 * 10 + 1 + 2 * 3 + 5 * 2" 耗时：3.187秒
             runner.execute("10 * 10 + 1 + 2 * 3 + 5 * 2", null, false,null);
            在打开 预编译缓存开关的情况下， 执行10万次 "10 * 10 + 1 + 2 * 3 + 5 * 2" 耗时：  0.171秒
             runner.execute("10 * 10 + 1 + 2 * 3 + 5 * 2", null, true,null);       
            运行引擎是线程安全的。在业务系统中实际使用过程中应该打开缓存预编译的开关，性能会更加。
             可以调用clearExpressCache()清除缓存 。
           在开启开关的情况下，会缓存解析后的最后执行指令如下所示，
           避免了字符串解析、词法分析、语法分析等步骤，简单对比一下，会提高30倍的速度：
        1:LoadData 10
		2:LoadData 10
		3:OP : * OPNUMBER[2] 
		4:LoadData 1
		5:OP : + OPNUMBER[2] 
		6:LoadData 2
		7:LoadData 3
		8:OP : * OPNUMBER[2] 
		9:OP : + OPNUMBER[2] 
		10:LoadData 5
		11:LoadData 2
		12:OP : * OPNUMBER[2] 
		13:OP : + OPNUMBER[2]     
   9、这个表达式相对别的计算工具，有点主要体现在：
      A、不需要预先加载可能需要的所有属性值
      B、 用户可以根据业务需要自定义操作符号和函数 
      C、可以同步输出判断错误信息，有利于提高业务系统在规则判断等使用场景下的用户体验。减少业务系统相关的处理代码。
   10、后续可以进一步优化的地方：
      A、现有的单词拆解、词法分析、语法分析都是自己写的山寨版，可以利用其他成熟的开源工具。
      B、优化具体的操作指令，提高单个操作符号的运行效率
      C、经过简单的扩展支持自定义代码片段的运行。               
 */
@SuppressWarnings("unchecked")
public class ExpressRunner
{

  private static final Log log = LogFactory.getLog(ExpressRunner.class);
  private boolean isTrace = false;
  private Map<String,InstructionSet> expressInstructionSetCache = new ConcurrentHashMap<String, InstructionSet>();
  
  protected OperatorManager m_operatorManager =  new OperatorManager();
  protected Map m_cacheOracleParseString = new HashMap();

	public ExpressRunner() {
	}

	public ExpressRunner(boolean aIsTrace) {
		this.isTrace = aIsTrace;
	}

  /**
	 * 为已经存在的操作符定义别名，主要用于定义不同的错误信息输出。例如  addOperatorWithAlias("属于","in","用户不在列表中")
	 * 
	 * @param aAliasName
	 *            操作符号别名
	 * @param name
	 *            原始操作符号别名
	 * @param errorInfo
	 *            当操作执行结果为false的时候输出的错误信息
	 * @throws Exception
	 */
	public void addOperatorWithAlias(String aAliasName, String name,
			String errorInfo) throws Exception {
		this.m_operatorManager
				.addOperatorWithAlias(aAliasName, name, errorInfo);
	}
    /**
     * 用户定义的操作符号优先级都设置为最高,操作数为两个
     * @param name 
     * @param op
     */
    public void addOperator(String name,Operator op){
    	this.m_operatorManager.addOperator(name,op);
    }
    /**
     * 增加用户的操作函数，例如group,定义如下
     * addFunction("group",new GroupOperator("group"))
     * 则表达式 ： group(2,3,4) 执行结果   9
     * 操作类定义如下：
     * public class GroupOperator extends Operator {
	   public GroupOperator(String aName) {
		this.name =aName;
	   }
	   public OperateData execute(Operation parent, OperateData[] list,List errorList)
			throws Exception {
		Object result = new Integer(0);
		for (int i = 0; i < list.length; i++) {
			result = OperatorOfNumber.Add.execute(result, list[i]
					.getObject(parent));
		}
		return new OperateData(result, result.getClass());
	    }
      }
     * @param name 操作函数名称
     * @param op 自定的操作符号
     */
    public void addFunction(String name, Operator op) {
    	this.m_operatorManager.addFunction(name, op);
    };
    
    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     * @param name 函数名称
     * @param aClassName 类名称
     * @param aFunctionName 类中的方法名称
     * @param aParameterTypes 方法的参数类型名称
     * @param errorInfo 如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name,String aClassName, String aFunctionName,
                        String[] aParameterTypes,String errorInfo) throws Exception {
    	this.m_operatorManager.addFunctionOfClassMethod(name, aClassName, aFunctionName, aParameterTypes, errorInfo);
    }
    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterTypes
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name,Object aServiceObject, String aFunctionName,
                        String[] aParameterTypes,String errorInfo ) throws Exception {
    	this.m_operatorManager.addFunctionOfServiceMethod(name, aServiceObject, aFunctionName, aParameterTypes, errorInfo);
    }
 

  protected Object[] getOpObjectList(String[] tmpList)  throws Exception
  {
      
	ExpressImport  tmpImportPackage = new ExpressImport();  
    List  list = new ArrayList();
    int point=0;
    
    //先处理import，import必须放在文件的最开始，必须以；结束
    boolean isImport = false;
    StringBuffer importName = new StringBuffer();
    while(point <tmpList.length ){
      if(tmpList[point].equals("import") ==true){
    	  isImport = true;
    	  importName.setLength(0);
      }else if(tmpList[point].equals(";") ==true) {
    	  isImport = false;
    	  tmpImportPackage.addPackage(importName.toString());
      }else if(isImport == true){
    	  importName.append(tmpList[point]);
      }else{
    	  break;
      }
      point = point + 1;
    }
    
    while(point <tmpList.length){
      String name = tmpList[point];
      if (name.equals("new") == true) {
        list.add(new ExpressItemNew());
        point = point + 1;
      }else if(tmpList[point].equals(".")){
         if(tmpList[point + 2].equals("(") == true){
           list.add(new ExpressItemMethod("method",tmpList[point + 1]));
         }else{
           list.add(new ExpressItemField("field",tmpList[point + 1]));
         }
         point = point + 2;
      }else if (this.m_operatorManager.isOperator(name)) { //判断是否操作符
      	if(name.equals("-")
      			&& (list.size() ==0 || list.size()>0 && list.get(list.size() -1 ) instanceof ExpressItem)
      	    && (point <tmpList.length -1) 
      	    && ( tmpList[point + 1].charAt(0) >='0'
      	    	   && tmpList[point + 1].charAt(0) <='9'
      	    	  ||tmpList[point + 1].charAt(0)=='.')
      	    && (point == 0 || this.m_operatorManager.isOperator(tmpList[point -1]) == true)	){ //对负数进行特殊处理
      		tmpList[point + 1] = '-' + tmpList[point + 1];
      		point = point + 1;
      		
      	}else{
          list.add(new ExpressItem(name,this.m_operatorManager.getOperatorRealName(name)));
          point = point + 1;
      	}
      }else if (name.charAt(0) == ':') {
    	throw new Exception("表达式中已经去处对参数的支持");  
       // list.add(new OperateDataParameter(name.substring(1)));
       // point = point + 1;
      }else if (name.charAt(0) == '\'' || name.charAt(0) == '"') {
        list.add(new OperateData(name.substring(1, name.length() - 1), String.class));
        point = point + 1;
      } else if ( (name.charAt(0) >= '0' && name.charAt(0) <= '9') ||
               (name.charAt(0) == '.') || name.charAt(0)=='-') { //数字       	
       if (name.endsWith("L") || name.endsWith("l")) {//long
         list.add(new OperateData(new Long(name.substring(0,name.length() - 1)), Long.TYPE));
       }else {
         boolean isFind = false;
         for (int i = 0; i < name.length(); i++){
           if (name.charAt(i) == '.'){//double
             list.add(new OperateData(new Double(name), Double.TYPE));
             isFind = true;
             break;
           }
         }
         if (isFind == false){//int
           list.add(new OperateData(new Integer(name), Integer.TYPE));
         }
       }
       
       point = point + 1;
     }else if(name.toLowerCase().equals("true")){
       list.add(new OperateData(Boolean.valueOf(true),Boolean.TYPE));
       point = point + 1;
     }else if (name.toLowerCase().equals("false")){
       list.add(new OperateData(Boolean.valueOf(false), Boolean.TYPE));
       point = point + 1;
      }else{
        boolean isClass = false;
        int j = point;
        Class tmpClass = null;
        String tmpStr="";
        while (j < tmpList.length) {
          tmpStr = tmpStr + tmpList[j];
          tmpClass = tmpImportPackage.getClass(tmpStr);
          if (tmpClass != null) {
            point = j + 1;
            isClass = true;
            break;
          }
          if(j < tmpList.length - 1 && tmpList[j+1].equals(".")==true){
            tmpStr = tmpStr + tmpList[j+1];
            j = j + 2;
            continue;
          }else{
            break;
          }
        }
        if(isClass == true){
        	//处理数组问题
        	String arrayStr ="";
        	int tmpPoint = point ;
			while (tmpPoint < tmpList.length) {
				if (tmpList[tmpPoint].equals("[")&& tmpList[tmpPoint + 1].equals("]")) {
				  arrayStr = arrayStr + "[]";
				  tmpPoint = tmpPoint + 2;
				} else {
					break;
				}
			}
			if(arrayStr.length() >0){
				tmpStr = tmpStr+ arrayStr;
				tmpClass = ExpressUtil.getJavaClass(tmpStr);
				point = tmpPoint;
			}
        }

        if(isClass == true){
          //处理造型操作(Integer);
          //前面有其它操作符合
          if(list.size() >0 && point < tmpList.length && list.get(list.size() -1) instanceof ExpressItem
             && ((ExpressItem)list.get(list.size() -1)).name.equals("(")==true
             && tmpList[point].equals(")")==true){
            //移出前一个“（”操作
            list.remove(list.size() -1 );
            list.add(new OperateClass(tmpStr,tmpClass));
            list.add(new ExpressItem("cast"));
            point = point + 1;//不在处理后一个“）”
          }else if( ( list.size() ==0
        		     || (list.get(list.size() -1)  instanceof ExpressItem == false)
        		     ||    this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("def") == false
        		        && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("new") == false
        		        && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("exportDef") == false
                    )
        		  && point < tmpList.length  && tmpList[point].equals("(") == false && 
        		  tmpList[point].equals(")") == false && tmpList[point].equals(".") == false ){//处理 int a
        	//处理 int a和def int a和 a=1; int b和new String( 三种情况
        	list.add(new ExpressItem("def"));
        	list.add(new OperateClass(tmpStr,tmpClass));  
          }else{//不是造型操作
            list.add(new OperateClass(tmpStr,tmpClass));
          }
        }else{
        	//将 def，alias 后面的第一个参数当作字符串处理
        	if(list.size() >=1 && list.get(list.size() -1 ) instanceof ExpressItem
        	         && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1 )).name).equalsIgnoreCase("alias")
        	        ){
        		list.add(new OperateData(name,String.class));
        	}else if(list.size() >=1 && list.get(list.size() -1 ) instanceof ExpressItem
           	         && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1 )).name).equalsIgnoreCase("exportAlias")
           	        ){
           		list.add(new OperateData(name,String.class));
        	}else if(list.size() >=1 && list.get(list.size() -1 ) instanceof ExpressItem
              	   &&  this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1 )).name).equalsIgnoreCase("macro")
              	   ){	
         		list.add(new OperateData(name,String.class));
        	}else if(list.size() >=1 && list.get(list.size() -1 ) instanceof ExpressItem
               	   &&  this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1 )).name).equalsIgnoreCase("function")
               	   ){	
          		list.add(new OperateData(name,String.class));
        	}else if(list.size() >=2 && list.get(list.size() -2 ) instanceof ExpressItem
             	   &&  this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -2 )).name).equalsIgnoreCase("def")
             	   ){	
        		list.add(new OperateData(name,String.class));
         	}else if(list.size() >=2 && list.get(list.size() -2 ) instanceof ExpressItem
              	   &&  this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -2 )).name).equalsIgnoreCase("exportdef")
              	   ){	
         		list.add(new OperateData(name,String.class));
         	}else if(point + 1 < tmpList.length && tmpList[point + 1].equals("(")){
        		list.add(new ExpressItemSelfDefineFunction(name));
            }else{
                list.add(new OperateDataAttr(name));
        	}
           point = point + 1;
        }
      }
    }
    return list.toArray();
  }
  
  
  
  public int matchNode(Object[] list,int startIndex,String opName){
	  Stack<Object> stack = new Stack<Object>();
	  if(opName.equalsIgnoreCase("(")){
		  for(int i = startIndex + 1 ; i < list.length;i++){
			  if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equals("(")){
				  stack.add(list[i]);
			  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equals(")")){
				  if(stack.size() >0){
					  stack.pop();
				  }else{
					  return i;
				  }
			  }
		  }
	  }	  
	  return -1;
  }
  /**
   * 通过"{","}",";"把程序拆分开
   * @param list
   * @return
   * @throws Exception
   */
  protected ExpressTreeNodeRoot getCResult(Object[] list) throws Exception{
	  Stack<List<Object>> stackList = new Stack<List<Object>> ();
	  Stack<ExpressTreeNodeRoot> nodeStack = new Stack<ExpressTreeNodeRoot> ();
	  nodeStack.push(new ExpressTreeNodeRoot("ROOT","main{\n","}\n",true));
	  stackList.push(new ArrayList<Object>());
	  for(int i=0;i< list.length;i++){
		  if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase(";")){//生成一个新的语句
			    ExpressTreeNode temp = new ExpressTreeNodeRoot(";","",";\n",false);
			    nodeStack.peek().addChild(temp);
			  for(Object item:stackList.peek().toArray()){
				  temp.addChild((ExpressTreeNode)item);
			  }
			  stackList.peek().clear();
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("{")){//生成一个新的语句
			  nodeStack.push(new ExpressTreeNodeRoot("{}","{\n","}\n",true));
			  stackList.push(new ArrayList<Object>());
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("}")){//生成一个新的语句
			  //若果 } 前面没有 ;则也作为一个完成的语句处理
			  if(false == (list[i - 1] instanceof ExpressItem == true &&  ((ExpressItem)list[i - 1]).name.equalsIgnoreCase(";")))
			  {   ExpressTreeNode temp = new ExpressTreeNodeRoot(";","",";\n",false);
			      nodeStack.peek().addChild(temp);
			      for(Object item:stackList.peek().toArray()){
			    	  temp.addChild((ExpressTreeNode)item);
			      }
		      }
			  //处理 }
			  stackList.peek().clear();
			  stackList.pop();
			  stackList.peek().add(nodeStack.pop());
			  //对应{},如果前面不是if(true)then{}else{},则生成一个新的语句
			  if(i + 1 < list.length ){
				  List tmpList = stackList.peek();
				  if((list[i+1] instanceof ExpressItem  == true )
						&& (((ExpressItem)list[i+1]).name.equalsIgnoreCase(";")==false
						  || ((ExpressItem)list[i+1]).name.equalsIgnoreCase("else")==false 
						  ) ){
					  //什么都不做
				  }else{
					  list[i] = new ExpressItem(";");
					  i = i -1;
				  }
			  }
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("[")){//生成一个新的语句
			  List tmpList = stackList.peek();
			  int arrarDim = 0;
			  for(int index = tmpList.size() -1;index >=0;index--){
				  if(tmpList.get(index) instanceof OperateClass ){
					  String className =ExpressUtil.getClassName(((OperateClass)tmpList.get(index)).getVarClass());
					  Class arrayClass = ExpressUtil.getJavaClass(className +"[]");
					  ((OperateClass)tmpList.get(index)).reset(className +"[]",arrayClass);
				  }else if(tmpList.get(index) instanceof ExpressTreeNodeRoot  && 
						  ((ExpressTreeNodeRoot)tmpList.get(index)).name.equals("[]")){
					  arrarDim = arrarDim + 1;
				  }else if(tmpList.get(index) instanceof  ExpressItemNew){
					  ((ExpressItemNew)tmpList.get(index)).opDataNumber = ((ExpressItemNew)tmpList.get(index)).opDataNumber + arrarDim ;
					  break;
				  }else{
					  stackList.peek().add(new ExpressItemArray("array"));
					  break;
				  }
			  }
			  nodeStack.push(new ExpressTreeNodeRoot("[]","[","]",false));
			  stackList.push(new ArrayList<Object>());			  
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("]")){//	
			  for(Object item:stackList.peek().toArray()){
				  nodeStack.peek().addChild((ExpressTreeNode)item);
			  }
			  stackList.peek().clear();			  
			  stackList.pop();
			  stackList.peek().add(nodeStack.pop());
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("(")){//生成一个新的语句
			  nodeStack.push(new ExpressTreeNodeRoot("()","","",false));
			  stackList.push(new ArrayList<Object>());
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase(")")){//			  
			  for(Object item:stackList.peek().toArray()){
				  nodeStack.peek().addChild((ExpressTreeNode)item);
			  }			  
			  stackList.peek().clear();			  
			  stackList.pop();
			  stackList.peek().add(nodeStack.pop());
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("for")){
			  if(list[i + 1] instanceof ExpressItem &&  ((ExpressItem)list[i + 1]).name.equalsIgnoreCase("(")){
				  ((ExpressItem)list[i + 1]).name = "{";
				  int finishPoint = matchNode(list,i + 1,"(");
				  ((ExpressItem)list[finishPoint]).name = "}";
			  }
			  stackList.peek().add(list[i]);
	      }else{
			  stackList.peek().add(list[i]);
		  }
	  }
	  ExpressTreeNodeRoot result = nodeStack.pop();
	  if(this.isTrace && log.isDebugEnabled()){
	     this.printTreeNode(result, 1);
	  }
	  result = (ExpressTreeNodeRoot)dealExpressTreeNodeRoot(result);
	  if(this.isTrace && log.isDebugEnabled()){
		     log.debug("----------处理后-------------");
		     this.printTreeNode(result, 1);
		  }
	  return result;
  }
  
  protected ExpressTreeNode dealExpressTreeNodeRoot(ExpressTreeNode root)throws Exception{
	  ExpressTreeNode[] children = root.getChildren();
	  if(children.length ==0){
		  return root;
	  }
	  for(int i=0;i<children.length;i++){
		  ExpressTreeNode item = children[i];
		  if(item instanceof ExpressTreeNodeRoot){
			  ExpressTreeNodeRoot tmpRoot = (ExpressTreeNodeRoot)item;
			     dealExpressTreeNodeRoot(tmpRoot);
		  }
	  }
	  
	  if(root instanceof ExpressTreeNodeRoot && ((ExpressTreeNodeRoot)root).name.equals("{}")){
		  return root;
	  }
	  if(root instanceof ExpressTreeNodeRoot && ((ExpressTreeNodeRoot)root).name.equals("ROOT")){
		  return root;
	  }	  
	  ExpressTreeNode[] tmpList = new ExpressTreeNode[children.length + 1];
	  System.arraycopy(children, 0, tmpList, 0, children.length);
	  //处理一句话
	  tmpList[tmpList.length - 1] = new ExpressItem(";");
	  root.setChildren(this.getCResultOne(tmpList));	  
	  return root;
  }
  
  protected ExpressTreeNode[] getCResultOne(Object[] list) throws Exception
  {
	try{
    if (list == null){
      throw new Exception("表达式不能为空");
    }  
    List<ExpressTreeNode> result = new ArrayList<ExpressTreeNode>();
    Stack sop = new Stack();
    Stack sdata = new Stack();
    Stack sOpDataNumber = new Stack();
    sop.push(new ExpressItem(";"));
    int i =0;
    while (i < list.length)
    {
      if(list[i] instanceof ExpressTreeNodeRoot){
    	  sdata.push(list[i]);
    	  i++;
      }else if (list[i] instanceof OperateData){  
    	 ((OperateData)list[i]).setMaxStackSize(sdata.size());
         sdata.push(list[i]);
         i++;
      }
      else if (list[i] instanceof ExpressItem)
      {
         ExpressItem op1 = (ExpressItem)sop.peek();
         ExpressItem op2 = (ExpressItem)list[i] ;
         int op  =m_operatorManager.compareOp(op1.name,op2.name);
         switch(op)
         {  case (0):
                    sop.push(list[i]);
                    i++;
                    break;
            case (2):// ( 与 ) 相遇
                     sop.pop();
                     if(this.isNAddOneParameterCount( (ExpressItem)sop.peek())){
                       ((ExpressItem) sop.peek()).opDataNumber = 1 + ( (Integer)sOpDataNumber.pop()).intValue();
                     }else{
                       ((ExpressItem) sop.peek()).opDataNumber = ( (Integer)sOpDataNumber.pop()).intValue();
                     }
                     if (list[i - 1] instanceof ExpressItem && ((ExpressItem)list[i - 1]).name.equals("(") == true) {
                       ((ExpressItem) sop.peek()).opDataNumber = ((ExpressItem) sop.peek()).opDataNumber - 1;
                     }
                     i++;
                     break;
            case(4):// if 与 then
            	op1.opDataNumber = 2;
                i++;
            	break;
            case(5):// if 与 else 
            	if(list[i -1] instanceof ExpressItem &&  ((ExpressItem)list[i -1]).name.equalsIgnoreCase("then") == true){
            		 //在if () then else 的情况下 增加一个 void 操作数，方便后续的错误判断
            	   sdata.push(new OperateData(null,void.class));            	     
            	}
            	op1.opDataNumber = 3;
                i++;
            	break;
            case (3):
						if (sop.size() > 1 || sdata.size() > 1) {
							throw new Exception("表达式设置错误，请检查函数名称是否匹配");
						}
						if (sdata.size() > 0) {//在break，continue的情况下没有数据
							ExpressTreeNode tmpResultNode = (ExpressTreeNode) sdata
									.pop();
							if (tmpResultNode instanceof MyPlace) {
								tmpResultNode = ((MyPlace) tmpResultNode).op;
							}
							result.add(tmpResultNode);
						}
						return result.toArray(new ExpressTreeNode[0]);
            case(7) :
            {
						ExpressTreeNode tmpNode = (ExpressTreeNode) sdata.pop();
						if (tmpNode instanceof MyPlace) {
							tmpNode = ((MyPlace) tmpNode).op;
						}
						result.add(tmpNode);
						i++;
            	break;
            }
            case(1) :
               sop.pop();//抛出堆栈顶的操作符号
               if (op1.name.equals(",")){
            	    throw new Exception("表达式配置错误"); 
               }else{
                   // 为了判断表达式的语法是否正确，需要对操作数栈进行处理，不支持(2,3,4)执行结果为4的形式     
                   int opDataNumber =m_operatorManager.getDataMember(op1.name);   
                   if (opDataNumber <0 && op1.opDataNumber >=0){
                       opDataNumber = op1.opDataNumber;
                   } 
                   if(op1.name.equalsIgnoreCase("return") == true ){
                	   opDataNumber = sdata.size(); 
                	   if(opDataNumber > 1){
                		   opDataNumber = 1; 
                	   }
                   }
                   
                   op1.opDataNumber = opDataNumber;
                   op1.setMaxStackSize(sdata.size());
                   List<ExpressTreeNode> tmpList = new ArrayList<ExpressTreeNode>();
            	   if(sdata.size() > 0 && sdata.peek() instanceof ExpressTreeNodeRoot && ((ExpressTreeNodeRoot) sdata.peek()).name.equals("()")){
              	    	 opDataNumber = opDataNumber + ((ExpressTreeNodeRoot) sdata.peek()).getChildCount()  - 1;
              	   }
                   for(int point =0;point < op1.opDataNumber;point++){
                  	 //确定操作符号的操作数
                  	 ExpressTreeNode tmpNode = (ExpressTreeNode)sdata.pop();
                  	 if(tmpNode instanceof MyPlace){
                  		 tmpNode = ((MyPlace)tmpNode).op;
                  	 }
                  	 tmpNode.setParent(op1);
                     tmpList.add(0,tmpNode);
                   }
                   op1.opDataNumber = opDataNumber;//重新计算了()中的操作数
                   op1.setChildren(tmpList.toArray(new ExpressTreeNode[0]));
                   
                   //这个判断可能存在潜在问题
                   if(op1.getChildren().length <=0 ){
                	   result.add(op1);
                   }else{
                      sdata.push(new MyPlace(op1));
                   }
               }
               break;
         }
      }
    }
	  }catch(Exception e){
		  throw new Exception("表达式语法分析错误：[" + e.getMessage() +"] 请检查：[" + getPrintInfo2(list," ")+"]",e);
	  }
	throw new Exception("没有能够进行正确的解析表达式");
    
  }

  protected InstructionSet createInstructionSet(ExpressTreeNodeRoot root,String type)throws Exception {
		InstructionSet result = new InstructionSet(type);
		createInstructionSet(root,result);
		return result;
	}
  protected void createInstructionSet(ExpressTreeNodeRoot root,InstructionSet result)throws Exception {
		Stack<ForRelBreakContinue> forStack = new Stack<ForRelBreakContinue>();
	    createInstructionSetPrivate(result,forStack,root,true);
	    if(forStack .size() > 0){
	    	throw new Exception("For处理错误");
	    }
	}  
	protected boolean createInstructionSetPrivate(InstructionSet result,Stack<ForRelBreakContinue> forStack,ExpressTreeNode node,boolean isRoot)throws Exception {
		boolean returnVal = false;
		if(node instanceof OperateDataAttr){
			FunctionInstructionSet functionSet = result.getMacroDefine(((OperateDataAttr) node).getName());
			if(functionSet != null){//是宏定义
				result.insertInstruction(result.getCurrentPoint()+1, new InstructionCallMacro(((OperateDataAttr) node).getName()));
			}else{
			  result.addLoadAttrInstruction((OperateDataAttr)node,node.getMaxStackSize());	
			  if(node.getChildren().length >0){
				  throw new Exception("表达式设置错误");
			  }
			}  
		}else if(node instanceof OperateData){			  	
		  result.addLoadDataInstruction((OperateData)node,node.getMaxStackSize());	
		  if(node.getChildren().length > 0){
			  throw new Exception("表达式设置错误");
		  }
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("function")){			  	
			  createInstructionSetForFunction(result,forStack,(ExpressItem)node);
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("for")){			  	
			  createInstructionSetForLoop(result,forStack,(ExpressItem)node);
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("cache")){			  	
			result.insertInstruction(result.getCurrentPoint()+1, new InstructionCachFuncitonCall());
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("macro")){
			createInstructionSetForMacro(result,(ExpressItem)node);
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("break")){
			InstructionGoTo breakInstruction = new InstructionGoTo(result.getCurrentPoint()+1);		
			breakInstruction.name = "break";
			forStack.peek().breakList.add(breakInstruction);
			result.insertInstruction(result.getCurrentPoint()+1, breakInstruction);
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("continue")){
			InstructionGoTo continueInstruction = new InstructionGoTo(result.getCurrentPoint()+1);		
			continueInstruction.name = "continue";
			forStack.peek().continueList.add(continueInstruction);
			
			result.insertInstruction(result.getCurrentPoint()+1, continueInstruction);

		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("call")){
			for(ExpressTreeNode tmpNode :node.getChildren()){
				createInstructionSetPrivate(result,forStack,tmpNode,false);					
			}
			result.insertInstruction(result.getCurrentPoint()+1, new InstructionCallFunction());
 		}else if(node instanceof ExpressItemSelfDefineFunction){//selfFunction
			createInstructionSetPrivate(result,forStack,node.getChildren()[0],false);
			ExpressItemSelfDefineFunction tmpNode = ((ExpressItemSelfDefineFunction)node);			
			result.insertInstruction(result.getCurrentPoint()+1, new InstructionCallSelfDefineFunction(tmpNode.functionName,tmpNode.opDataNumber));
		}else if(node instanceof ExpressItem && ((ExpressItem)node).name.equalsIgnoreCase("if")){
			returnVal = createInstructionSetForIf(result,forStack,(ExpressItem)node);			
		}else if(node instanceof ExpressTreeNodeRoot){
			int tmpPoint = result.getCurrentPoint()+1;
			boolean hasDef = false;
			for(ExpressTreeNode tmpNode : ((ExpressTreeNodeRoot)node).getChildren()){
				//如果是";"操作符号，则需要增加清除堆栈的操作
				if (((ExpressTreeNodeRoot) node).name.equals(";")) {
					if (result.getCurrentPoint() >= 0
							&& (result.getInstruction(result.getCurrentPoint()) instanceof InstructionClearDataStack == false)) {
						result.insertInstruction(result.getCurrentPoint() + 1,
								new InstructionClearDataStack());
					}
				}
				boolean tmpHas =   createInstructionSetPrivate(result,forStack,tmpNode,false);
				hasDef = hasDef || tmpHas;
			}
			if (hasDef == true&& isRoot == false
					&& ((ExpressTreeNodeRoot) node).name.equals("{}")){
				result.insertInstruction(tmpPoint,new InstructionOpenNewArea());
				result.insertInstruction(result.getCurrentPoint() + 1,new InstructionCloseNewArea());
				returnVal = false;
			}else{
				returnVal = hasDef;
			}
		}else if(node instanceof ExpressItem){
			ExpressItem tmpExpressItem = (ExpressItem)node;
			OperatorBase op = m_operatorManager.newInstance(tmpExpressItem);
			ExpressTreeNode[] children = node.getChildren();
			int [] finishPoint = new int[children.length];
			for(int i =0;i < children.length;i++){
				ExpressTreeNode tmpNode = children[i];
				boolean tmpHas =  createInstructionSetPrivate(result,forStack,tmpNode,false);
				returnVal = returnVal || tmpHas;
				finishPoint[i] = result.getCurrentPoint();
			}
			
			if(op instanceof OperatorReturn){
				result.insertInstruction(result.getCurrentPoint()+1,new InstructionReturn());	
			}else{	
			   result.addOperatorInstruction(op,tmpExpressItem.opDataNumber,tmpExpressItem.getMaxStackSize());
				if(op instanceof OperatorAnd){
					result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(false,result.getCurrentPoint() - finishPoint[0] + 1,false));
				}else if(op instanceof OperatorOr){
					result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(true,result.getCurrentPoint() - finishPoint[0] + 1,false));
				}else if(op instanceof OperatorDef || op instanceof OperatorAlias){
					returnVal = true;
				}else if(op instanceof OperatorExportDef){
					//添加对外的变量声明
					result.addExportDef(new ExportItem(children[1].toString(),ExportItem.TYPE_DEF,ExpressUtil.getClassName(((OperateClass)children[0]).getVarClass())));
				}else if(op instanceof OperatorExportAlias ){
					result.addExportDef(new ExportItem(children[0].toString(),ExportItem.TYPE_ALIAS,children[1].toResource()));
				}
			}
		}else{
			throw new Exception("不支持的数据类型:" + node.getClass());
		}
		return returnVal;
	}
	/**
	 * 创建宏指令
	 * @param result
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected boolean createInstructionSetForMacro(InstructionSet result,ExpressItem node)throws Exception {
		String macroName =(String)((OperateData)node.getChildren()[0]).dataObject;
		ExpressTreeNodeRoot macroRoot = new ExpressTreeNodeRoot("macro-" + macroName,"macro " + macroName +"{\n","}\n",true);
		macroRoot.addChild(node.getChildren()[1]);
		InstructionSet macroInstructionSet = this.createInstructionSet(macroRoot,InstructionSet.TYPE_MARCO);
		result.addMacroDefine(macroName, new FunctionInstructionSet(macroName,"macro",macroInstructionSet));
		return false;
	}

	/**
	 * 生成 if 的指令集合
	 * @param result
	 * @param node
	 * @throws Exception
	 */
	protected boolean createInstructionSetForIf(InstructionSet result,Stack<ForRelBreakContinue>  forStack,ExpressItem node)throws Exception {
    	ExpressTreeNode[] children = node.getChildren();
    	if(children.length < 2){
    		throw new Exception("if 操作符至少需要2个操作数 " );
    	}else if (children.length == 2) {
    		//补充一个分支
    		ExpressTreeNode[] oldChilder =  children;
    		children = new ExpressTreeNode[3];
    		children[0] = oldChilder[0];
    		children[1] = oldChilder[1];
    		children[2] = new OperateData(null,void.class);    			
    	}else if(children.length > 3){
    		throw new Exception("if 操作符最多只有3个操作数 " );
    	}
		int [] finishPoint = new int[children.length];
   		boolean r1 = createInstructionSetPrivate(result,forStack,children[0],false);//condition	
		finishPoint[0] = result.getCurrentPoint();
		boolean r2 = createInstructionSetPrivate(result,forStack,children[1],false);//true		
		result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(false,result.getCurrentPoint() - finishPoint[0] + 2,true));
		finishPoint[1] = result.getCurrentPoint();
		boolean r3 = createInstructionSetPrivate(result,forStack,children[2],false);//false
		result.insertInstruction(finishPoint[1]+1,new InstructionGoTo(result.getCurrentPoint() - finishPoint[1] + 1));  		
        return r1 || r2 || r3;
	}
	protected boolean createInstructionSetForFunction(InstructionSet result,Stack<ForRelBreakContinue>  forStack,ExpressItem node)throws Exception {
    	ExpressTreeNode[] children = node.getChildren();
    	if(children.length != 3){
    		throw new Exception("funciton 操作符需要3个操作数 " );
    	}
    	InstructionSet functionSet = new InstructionSet(InstructionSet.TYPE_FUNCTION);
    	for(ExpressTreeNode item : children[1].getChildren()){
    		ExpressItem expressItem = (ExpressItem)item;
    		if(expressItem.name.equals("def") == false){
    			throw new Exception(expressItem +" 不是一个本地变量定义");
    		}
    		Class varClass = ((OperateClass)expressItem.getChildren()[0]).getVarClass();
    		String varName = (String)((OperateData)expressItem.getChildren()[1]).dataObject;
    		OperateDataLocalVar tmpVar = new OperateDataLocalVar(varName,varClass);
    		functionSet.addParameter(tmpVar);
    	}
    	
		String functionName =(String)((OperateData)node.getChildren()[0]).dataObject;
		ExpressTreeNodeRoot macroRoot = new ExpressTreeNodeRoot("function-" + functionName,"function "+functionName +"(...){\n","}\n",true);
		macroRoot.addChild(node.getChildren()[2]);
		this.createInstructionSet(macroRoot,functionSet);
		result.addMacroDefine(functionName, new FunctionInstructionSet(functionName,"function",functionSet));
		return false;
	}
	protected boolean createInstructionSetForLoop(InstructionSet result,Stack<ForRelBreakContinue> forStack,ExpressItem node)throws Exception {
    	if(node.getChildren().length < 2){
    		throw new Exception("if 操作符至少需要2个操作数 " );
    	}else if(node.getChildren().length > 2){
    		throw new Exception("if 操作符最多只有2个操作数 " );
    	}
    	if(node.getChildren()[0].getChildren()!= null && node.getChildren()[0].getChildren().length > 3){
    		throw new Exception("循环语句的设置不合适:" + node.getChildren()[0]);	
    	}
    	//生成作用域开始指令
	    result.insertInstruction(result.getCurrentPoint()+1, new InstructionOpenNewArea());			
	    forStack.push(new ForRelBreakContinue(node));
	    
    	//生成条件语句部分指令
    	ExpressTreeNode conditionNode = node.getChildren()[0];
    	int nodePoint = 0;
    	if (conditionNode.getChildren() != null && conditionNode.getChildren().length == 3){//变量定义，判断，自增都存在
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[0],false);
    		nodePoint = nodePoint + 1;
    	}
    	//循环的开始的位置
    	int loopStartPoint = result.getCurrentPoint()+ 1;
    	
    	//有条件语句
    	InstructionGoToWithCondition conditionInstruction=null;
    	if(conditionNode.getChildren() != null 
    		&& (conditionNode.getChildren().length == 1
    			|| conditionNode.getChildren().length == 2 
    			|| conditionNode.getChildren().length == 3)
    		)	{    		
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    		//跳转的位置需要根据后续的指令情况决定    		
    		conditionInstruction = new InstructionGoToWithCondition(false,-1,true);
    		result.insertInstruction(result.getCurrentPoint()+1,conditionInstruction);   
    		nodePoint = nodePoint+ 1;
    	}
    	int conditionPoint = result.getCurrentPoint();
    	//生成循环体的代码
    	createInstructionSetPrivate(result,forStack,node.getChildren()[1],false);
    	
    	int selfAddPoint = result.getCurrentPoint()+1;
    	//生成自增代码指令
    	if(conditionNode.getChildren()!= null &&(
    			conditionNode.getChildren().length == 2 || conditionNode.getChildren().length == 3
    			)){
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    	}
    	//增加一个无条件跳转
    	InstructionGoTo reStartGoto = new InstructionGoTo(loopStartPoint - (result.getCurrentPoint() + 1));
    	result.insertInstruction(result.getCurrentPoint()+1,reStartGoto); 
    	
    	//修改条件判断的跳转位置
    	if(conditionInstruction != null){
    	   conditionInstruction.offset = result.getCurrentPoint() - conditionPoint + 1;
    	}
    	
    	//修改Break和Continue指令的跳转位置,循环出堆
    	ForRelBreakContinue rel =  forStack.pop();
    	for(InstructionGoTo item:rel.breakList){
    		item.offset = result.getCurrentPoint() -  item.offset ;
    	}
    	for(InstructionGoTo item:rel.continueList){
    		item.offset = selfAddPoint -  item.offset - 1;
    	}    	
    	
    	//生成作用域结束指令
	    result.insertInstruction(result.getCurrentPoint()+1, new InstructionCloseNewArea());

        return false;
	}    
	
	
	protected void printTreeNode(ExpressTreeNode node, int level) {
		StringBuilder builder = new StringBuilder();
		builder.append(level+":" );
		
		for (int i = 0; i < level; i++) {
			builder.append("   ");
		}
		builder.append(node);
		if(builder.length() <100){
			for (int i = 0; i <100 - builder.length(); i++) {
				builder.append("   ");
			}
		}
		builder.append("\t"+ node.getClass().getName());
		
		ExpressTreeNode[] children = node.getChildren();
		System.out.println(builder);
		if (children.length ==0) {
			return ;
		}
		for (ExpressTreeNode item : children) {
			printTreeNode(item, level + 1);
		}
	}

	protected boolean isNAddOneParameterCount(ExpressItem operatorItem){
	  if(operatorItem instanceof ExpressItemMethod || operatorItem instanceof ExpressItemNew)
		  return true;
	  return this.m_operatorManager.isNAddOneParameterCount(operatorItem.name);
  }
  protected String getPrintInfo(Object[] list,String splitOp){
  	StringBuffer buffer = new StringBuffer();
	for(int i=0;i<list.length;i++){
		if(i > 0){buffer.append(splitOp);}
		buffer.append("{" + list[i] +"}");
	}
	return buffer.toString();
  }
  protected String getPrintInfo2(Object[] list,String splitOp){
	  	StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.length;i++){
			if(i > 0){buffer.append(splitOp);}
			buffer.append(list[i]);
		}
		return buffer.toString();
	  }

  protected ExpressTreeNodeRoot parseCResult(String condition)throws Exception{

		String[] tmpList = parse(condition);
	    if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("执行的表达式：" + condition);	    	
	    	log.debug("单词分解结果:" + getPrintInfo(tmpList,","));
	    }
	    Object[] tmpObjectList = getOpObjectList(tmpList);
	    if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("语法分解结果:" + getPrintInfo(tmpObjectList,","));
	    }
	    ExpressTreeNodeRoot result = getCResult(tmpObjectList);
	    return result;
}
  public InstructionSet parseInstructionSet(String condition)throws Exception{
	  InstructionSet result = createInstructionSet(this.parseCResult(condition),InstructionSet.TYPE_MAIN);
	  if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("生成的指令集:\n" + result);
	    }
	  return result;
	  
  }
  
	public Object execute(String expressString, IExpressContext context,
			List errorList, boolean isCache, boolean isTrace) throws Exception {
		return this.execute(expressString, context, errorList, isCache, isTrace, null);
	}
	public Object execute(String expressString,IExpressContext context,List errorList,
			boolean isCache,boolean isTrace,Log aLog) throws Exception {
		InstructionSet parseResult = null;
		if (isCache == true) {
			parseResult = expressInstructionSetCache.get(expressString);
			if (parseResult == null) {
				synchronized (expressInstructionSetCache) {
					parseResult = expressInstructionSetCache.get(expressString);
					if (parseResult == null) {
						parseResult = this.parseInstructionSet(expressString);
						expressInstructionSetCache.put(expressString,
								parseResult);
					}
				}
			}
		} else {
			parseResult = this.parseInstructionSet(expressString);
		}
		return this.execute(new InstructionSet[] { parseResult }, null,
				context, errorList, null, isTrace, false,aLog);
	}
  public Object execute(InstructionSet[] instructionSets,ExpressLoader loader,IExpressContext context,
		  List errorList,FuncitonCacheManager aFunctionCacheMananger,boolean isTrace,boolean isCatchException,
			Log aLog) throws Exception{
	 return  InstructionSet.execute(instructionSets,loader,context, errorList, aFunctionCacheMananger, isTrace,isCatchException,aLog);
  }

  /**
   * 清除缓存
   */
  public void clearExpressCache(){
	  this.expressInstructionSetCache.clear();
  }
  
  /**
   * 执行预处理后的表达式
   * @param context 计算可能需要的上下文，如果没有属性，可以是null
   * @param list 预处理后的表达式
   * @param errorList 输出错误信息的List
   * @return 计算的结果
   * @throws Exception
   */
  protected final Object executeWithPreCompile(InstructionSetContext context,Object[] expressItems,List errorList) throws Exception
  {
    if (expressItems == null)       
    	return null;
    Stack sdata = new Stack();
    int i =0;
    while (i < expressItems.length)
    {
      if (expressItems[i] instanceof OperateData)
      {
         sdata.push(expressItems[i]);
         i++;
      }else if (expressItems[i] instanceof ExpressItem){
         ExpressItem opItem = (ExpressItem)expressItems[i];
         OperatorBase op = m_operatorManager.newInstance(opItem);
         int opDataNumber =m_operatorManager.getDataMember(op.getName());
         if (opDataNumber <0)
           opDataNumber = opItem.opDataNumber;

         OperateData[] parameterList  = null;
         if (opDataNumber >=0)
            parameterList = new OperateData[opDataNumber];

         for(int index = opDataNumber - 1;index>=0;index--){
           parameterList[index] = (OperateData)sdata.pop();
         }
         OperateData TmpResult =op.execute(context,parameterList,errorList);
         sdata.push(TmpResult);
         i++;
      }
    }
    return ((OperateData)sdata.pop()).getObject(context);    

  }
  
  protected final ExpressTreeNode preCompileOperatorTree(Object[] expressItems) throws Exception
  { 
    if (expressItems == null){    
    	throw new Exception("表达式不能为空");
    }
    Stack sdata = new Stack();
    int i =0;
    while (i < expressItems.length)
    {
      if (expressItems[i] instanceof OperateData)
      {
         sdata.push(expressItems[i]);
         i++;
      }else if (expressItems[i] instanceof ExpressItem){
         ExpressItem opItem = (ExpressItem)expressItems[i];

         //设置准确的操作数数量
         int opDataNumber =m_operatorManager.getDataMember(opItem.name);
         if (opDataNumber <0){
           opDataNumber = opItem.opDataNumber;
         }
         opItem.opDataNumber = opDataNumber;         
         
         List<ExpressTreeNode> tmpList = new ArrayList<ExpressTreeNode>();         
         for(int index = opDataNumber - 1;index>=0;index--){
        	 //确定操作符号的操作数
        	 ExpressTreeNode tmpNode = (ExpressTreeNode)sdata.pop();
        	 if(tmpNode instanceof MyPlace){
        		 tmpNode = ((MyPlace)tmpNode).op;
        	 }
        	 tmpNode.setParent(opItem);
             tmpList.add(0,tmpNode);
         }
         opItem.setChildren(tmpList.toArray(new ExpressTreeNode[0]));
         sdata.push(new MyPlace(opItem));
         i++;
      }
    }
    ExpressTreeNode rootNode = (ExpressTreeNode)sdata.pop();
    if(rootNode instanceof MyPlace){
    	rootNode = ((MyPlace)rootNode).op;
    }
    return rootNode;
  }

   private  void splitOperator(String opStr,ArrayList ObjList){
     while (opStr.length() >0){ //处理操作符字串
          boolean isFind = false;
          int index =opStr.length();
          while (index > 0)
          {
              if (this.m_operatorManager.isOperator(opStr.substring(0,index)) == true)
              {
                 ObjList.add(opStr.substring(0,index));
                 opStr = opStr.substring(index);
                 isFind = true;
                 break;
              }
              else
                index = index - 1;
          }
          if(isFind == false)
             opStr = opStr.substring(1);
     }
  }
   protected boolean isNumber(String str){
    if(str == null || str.equals(""))
      return false;
    char c = str.charAt(0);
    if (c >= '0' && c <= '9') { //数字
      return true;
    }
    else {
      return false;
    }
  }
  /**
   * 文本分析函数，“.”作为操作符号处理
   * @param str String
   * @throws Exception
   * @return String[]
   */
   protected String[] parse(String str) throws Exception
  {

    if (str == null)
       return new String[0];
    str = str.trim();
    if(str.endsWith(";") == false ){
      str = str +";";
    }

    String tmpWord ="";
    String tmpOpStr ="";
    char c;
    ArrayList list = new ArrayList();
    int i= 0;
    while(i<str.length())
    {
       c = str.charAt(i);
      if (c=='"' || c=='\''){
        int index = str.indexOf(c,i + 1);
        if (index < 0)
        	throw new Exception("字符串没有关闭");
        //先将操作符填入队列，再填充操作数
        splitOperator(tmpOpStr,list);
        tmpOpStr="";
        if (tmpWord.length() >0){
            list.add(tmpWord);
            tmpWord  = "";
        }
        list.add(str.substring(i,index + 1));
        i = index + 1;
      }else if (((c >='0') && (c <='9'))
            || ((c >='a') && (c <='z'))
            || ((c >='A') && (c <='Z'))
            || (c=='\'')
            || (c=='$')
            || (c==':')
            || (c=='_')
            || (c > 127))  //标准字符
       {
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(tmpOpStr,list);
           tmpOpStr = "";
       }else if(c=='.' && this.isNumber(tmpWord) == true){
           //是数字，当数据处理
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(tmpOpStr,list);
           tmpOpStr = "";
       }else //遇到其他分割符
       {
         tmpOpStr = tmpOpStr + c;
         i = i + 1;
         if (tmpWord.length() >0)
         {    list.add(tmpWord);
              tmpWord = "";
         }
       }
    }

    if (tmpWord.length() >0)
    {    list.add(tmpWord);
         tmpWord = "";
    }
    splitOperator(tmpOpStr,list);

    String result[] = new String[list.size()];
    list.toArray(result);
    return result;
  }

}
