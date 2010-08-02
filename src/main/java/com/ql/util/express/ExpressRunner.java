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
  private Map<String,Object[]> expressParseResultCache = new HashMap<String,Object[]>();
  private Map<String,InstructionSet> expressInstructionSetCache = new HashMap<String,InstructionSet>();
  
  protected OperatorManager m_operatorManager =  new OperatorManager();;
  protected ExpressImport m_import = new ExpressImport();
  protected Map m_cacheOracleParseString = new HashMap();
  public ExpressRunner(){ }

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
 

  private Object[] getOpObjectList(String[] tmpList)  throws Exception
  {
   
    List  list = new ArrayList();
    int point=0;
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
      	    	  ||tmpList[point + 1].charAt(0)=='.')){ //对负数进行特殊处理
      		tmpList[point + 1] = '-' + tmpList[point + 1];
      		point = point + 1;
      		
      	}else{
          list.add(new ExpressItem(name));
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
       list.add(new OperateData(new Boolean(true),Boolean.TYPE));
       point = point + 1;
     }else if (name.toLowerCase().equals("false")){
       list.add(new OperateData(new Boolean(false), Boolean.TYPE));
       point = point + 1;
      }else{
        boolean isClass = false;
        int j = point;
        Class tmpClass = null;
        String tmpStr="";
        while (j < tmpList.length) {
          tmpStr = tmpStr + tmpList[j];
          tmpClass = this.m_import.getClass(tmpStr);
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
          //处理造型操作(Integer);
          //前面有其它操作符合
          if(list.size() >0 && point < tmpList.length && list.get(list.size() -1) instanceof ExpressItem
             && ((ExpressItem)list.get(list.size() -1)).name.equals("(")==true
             && tmpList[point].equals(")")==true){
            //移出前一个“（”操作
            list.remove(list.size() -1 );
            list.add(new OperatorClass(tmpStr,tmpClass));
            list.add(new ExpressItem("cast"));
            point = point + 1;//不在处理后一个“）”
          }else{//不是造型操作
            list.add(new OperatorClass(tmpStr,tmpClass));
          }
        }else{
          list.add(new OperateDataAttr(name));
          point = point + 1;
        }
      }
    }
    return list.toArray();
  }
  protected ExpressTreeNode getCResult(Object[] list) throws Exception
  {
	try{
    if (list == null){
      throw new Exception("表达式不能为空");
    }  
    Stack sop = new Stack();
    Stack sdata = new Stack();
    Stack sOpDataNumber = new Stack();
    sop.push(new ExpressItem(";"));
    int i =0;
    while (i < list.length)
    {
      if (list[i] instanceof OperateData)
      {  
    	 ((OperateData)list[i]).setMaxStackSize(sdata.size());
         sdata.push(list[i]);
         i++;
      }
      else if (list[i] instanceof ExpressItem)
      {
         ExpressItem op1 = (ExpressItem)sop.peek();
         ExpressItem op2 = (ExpressItem)list[i] ;
         int op  =m_operatorManager.compareOp(op1.name,op2.name);
         if (op2.name.equals("(")){
            sOpDataNumber.push(new Integer(1));            	
         }   

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
            case (3):
						if (sop.size() > 1 || sdata.size() > 1) {
							throw new Exception("表达式设置错误，请检查函数名称是否匹配");
						}
						ExpressTreeNode rootNode = (ExpressTreeNode)sdata.pop();
						if (rootNode instanceof MyPlace) {
							rootNode = ((MyPlace) rootNode).op;
						}
						return rootNode;
            case(1) :
               sop.pop();//抛出堆栈顶的操作符号
               if (op1.name.equals(",")){
                   int TmpInt = ((Integer)sOpDataNumber.pop()).intValue();
                   TmpInt = TmpInt + 1;
                   sOpDataNumber.push(new Integer(TmpInt));
               }else{
                   // 为了判断表达式的语法是否正确，需要对操作数栈进行处理，不支持(2,3,4)执行结果为4的形式
                   int opDataNumber =m_operatorManager.getDataMember(op1.name);
                   if (opDataNumber <0){
                     opDataNumber = op1.opDataNumber;
                   } 
                   op1.opDataNumber = opDataNumber;
                   op1.setMaxStackSize(sdata.size());
                   List<ExpressTreeNode> tmpList = new ArrayList<ExpressTreeNode>();         
                   for(int point =0;point < opDataNumber;point++){
                  	 //确定操作符号的操作数
                  	 ExpressTreeNode tmpNode = (ExpressTreeNode)sdata.pop();
                  	 if(tmpNode instanceof MyPlace){
                  		 tmpNode = ((MyPlace)tmpNode).op;
                  	 }
                  	 tmpNode.setParent(op1);
                     tmpList.add(0,tmpNode);
                   }
                   op1.setChildren(tmpList.toArray(new ExpressTreeNode[0]));
                   sdata.push(new MyPlace(op1));
               }
               break;
         }
      }
    }
	  }catch(Exception e){
		  throw new Exception("表达式语法分析错误：[" + e.getMessage() +"] 请检查：[" + getPrintInfo2(list," ")+"]");
	  }
    return null;
  }

  protected InstructionSet createInstructionSet(ExpressTreeNode node)throws Exception {
		InstructionSet result = new InstructionSet();
		createInstructionSetPrivate(result,node);
		result.insertInstruction(result.getCurrentPoint()+1, new InstructionReturn());
		return result;
		
	}
	protected void createInstructionSetPrivate(InstructionSet result,ExpressTreeNode node)throws Exception {
		if(node instanceof OperateData){
		  result.addLoadDataInstruction((OperateData)node,node.getMaxStackSize());	
		  if(node.getChildren() != null){
			  throw new Exception("表达式设置错误");
		  }
		  
		}else if(node instanceof ExpressItem){
			ExpressItem tmpExpressItem = (ExpressItem)node;
			OperatorBase op = m_operatorManager.newInstance(tmpExpressItem);

			ExpressTreeNode[] children = node.getChildren();
			int [] finishPoint = new int[children.length];
			for(int i =0;i < children.length;i++){
				ExpressTreeNode tmpNode = children[i];
				createInstructionSetPrivate(result,tmpNode);
				finishPoint[i] = result.getCurrentPoint();
			}
			result.addOperatorInstruction(op,tmpExpressItem.opDataNumber,tmpExpressItem.getMaxStackSize());
			if(op instanceof OperatorAnd){
				result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(false,result.getCurrentPoint() - finishPoint[0] + 1));
			}else if(op instanceof OperatorOr){
				result.insertInstruction(finishPoint[0]+1,new InstructionGoToWithCondition(true,result.getCurrentPoint() - finishPoint[0] + 1));
			}
		}else{
			throw new Exception("不支持的数据类型:" + node.getClass());
		}
	}

	protected void printTreeNode(ExpressTreeNode node, int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("   ");
		}
		
		System.out.println(node);
		ExpressTreeNode[] children = node.getChildren();
		if (children == null) {
			return;
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

  protected ExpressTreeNode parseCResult(String condition)throws Exception{

		String[] tmpList = parse(condition);
	    if(log.isDebugEnabled()){
	    	log.debug("执行的表达式：" + condition);	    	
	    	log.debug("单词分解结果:" + getPrintInfo(tmpList,","));
	    }
	    Object[] tmpObjectList = getOpObjectList(tmpList);
	    if(log.isDebugEnabled()){
	    	log.debug("语法分解结果:" + getPrintInfo(tmpObjectList,","));
	    }
	    ExpressTreeNode result = getCResult(tmpObjectList);
//	    if(log.isDebugEnabled()){
//	    	log.debug("后缀表达式:" + getPrintInfo(result,","));
//	    }
	    return result;
}
  public InstructionSet parseInstructionSet(String condition)throws Exception{
	  InstructionSet result = createInstructionSet(this.parseCResult(condition));
	  if(log.isDebugEnabled()){
	    	log.debug("生成的指令集:\n" + result);
	    }
	  return result;
	  
  }
  

  public Object execute(String expressString,List errorList,boolean isCache,IExpressContext context) throws Exception{
	  InstructionSet parseResult = null;
	  if(isCache == true){
	    parseResult = expressInstructionSetCache.get(expressString);
		if(parseResult == null){
			synchronized(expressParseResultCache){
				parseResult = expressInstructionSetCache.get(expressString);
				if(parseResult == null){
					parseResult = this.parseInstructionSet(expressString);
					expressInstructionSetCache.put(expressString, parseResult);
					//System.out.println(parseResult);
				}
			}
		}
	  }else{
		  parseResult = this.parseInstructionSet(expressString);
	  }
    return parseResult.excute(context,errorList);
  } 
  /**
   * 清除缓存
   */
  public void clearExpressCache(){
	  this.expressParseResultCache.clear();
  }
  
  /**
   * 执行预处理后的表达式
   * @param context 计算可能需要的上下文，如果没有属性，可以是null
   * @param list 预处理后的表达式
   * @param errorList 输出错误信息的List
   * @return 计算的结果
   * @throws Exception
   */
  protected final Object executeWithPreCompile(IExpressContext context,Object[] expressItems,List errorList) throws Exception
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
    if(str == null || str =="")
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
  private String[] parse(String str) throws Exception
  {

    if (str == null)
       return new String[0];
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

  public void addPackage(String aPackageName){
    this.m_import.addPackage(aPackageName);
  }
  public void removePackage(String aPackageName){
    this.m_import.removePackage(aPackageName);
  }
  public void resetPackages(){
    this.m_import.resetPackages();
  }

}




 class ExpressItem implements ExpressTreeNode {
   protected String name;
   protected int opDataNumber;
   public int point = -1;
   
   private int maxStackSize = 1; 
   private ExpressTreeNode parent;
   private ExpressTreeNode[] children;
   
   public ExpressItem(String aName){
     name = aName;
     this.opDataNumber = 0;
   }
   public String toString()
   {
	   return name;
   }
	public ExpressTreeNode getParent() {
		return parent;
	}

	public void setParent(ExpressTreeNode aParent) {
		this.parent = aParent;
	}
	public ExpressTreeNode[] getChildren() {
		return children;
	}
	public void setChildren(ExpressTreeNode[] children) {
		this.children = children;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}
 }
 class ExpressItemNew extends ExpressItem{
   public ExpressItemNew(){
     super("new");
   }
 }

 class ExpressItemField extends ExpressItem{
   protected String fieldName;
   public ExpressItemField(String aName,String aFieldName){
     super(aName);
     this.fieldName = aFieldName;
   }
   public String toString()
   {
     return "Operator:" + name +":" + this.fieldName+ " OperandNumber:" + this.opDataNumber;
   }
 }
 class ExpressItemMethod extends ExpressItem{
   protected String methodName;
   public ExpressItemMethod(String aName,String aMethodName){
     super(aName);
     this.methodName = aMethodName;
   }
   public String toString()
   {
     return "Operator:" + name +":" + this.methodName+ " OperandNumber:" + this.opDataNumber;
   }

 }

 @SuppressWarnings("unchecked")
 class ExpressImport {

 	protected List m_packages = new ArrayList();

 	public ExpressImport() {
 		this.resetPackages();
 	}

 	public void addPackage(String aPackageName) {
 		this.m_packages.add(aPackageName);
 	}

 	public void removePackage(String aPackageName) {
 		this.m_packages.remove(aPackageName);
 	}

 	public void resetPackages() {
 		this.m_packages.clear();
 		m_packages.add("java.lang");
 		m_packages.add("java.util");
 	}

 	public Class getClass(String name) {
 		Class result = null;
 		// 如果本身具有报名，这直接定位
 		if (name.indexOf(".") >= 0) {
 			try {
 				result = Class.forName(name);
 			} catch (ClassNotFoundException ex) {
 			}
 			return result;
 		}
 		if (Integer.TYPE.getName().equals(name) == true)
 			return Integer.TYPE;
 		if (Short.TYPE.getName().equals(name) == true)
 			return Short.TYPE;
 		if (Long.TYPE.getName().equals(name) == true)
 			return Long.TYPE;
 		if (Double.TYPE.getName().equals(name) == true)
 			return Double.TYPE;
 		if (Float.TYPE.getName().equals(name) == true)
 			return Float.TYPE;
 		if (Byte.TYPE.getName().equals(name) == true)
 			return Byte.TYPE;
 		if (Character.TYPE.getName().equals(name) == true)
 			return Character.TYPE;
 		if (Boolean.TYPE.getName().equals(name) == true)
 			return Boolean.TYPE;

 		for (int i = 0; i < m_packages.size(); i++) {
 			String tmp = (String) m_packages.get(i) + "." + name;
 			try {
 				result = Class.forName(tmp);
 			} catch (ClassNotFoundException ex) {
 				//
 			}
 			if (result != null) {
 				return result;
 			}
 		}
 		return null;
 	}
 }
