package com.ql.util.express;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 操作符管理类
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
final class OperatorManager {

	protected List<String> m_function = new ArrayList<String>();
	protected Map<String, OperatorBase> opMap = new HashMap<String, OperatorBase>();

  /**
    优先级是指同一式子中多个运算符被执行和次序，
    同一级里的操作符具有相同的优先级，
    相同优先级得操作符号相遇是，根据结合性决定运算次序，
    下面从高到低的优先级列出了运算符。
    同一行中的运算符优先级相同

    注意：在进行SQL语法处理时 = 表示 逻辑运算等于 优先级为 6 结合型为从左到右
          在进行JAVA语法处理时 = 表示 赋值 优先级为 12 结合型为从右到左
   */
	protected Map<String,OpStr> opStrList = new HashMap<String,OpStr>();

 public OperatorManager() {
	 
	 	this.addOperatorInner("cache", 0, 0, 0,  new OperatorAlias("cache"));
		this.addOperatorInner("def", 0, 0, 2,  new OperatorDef("def"));
		this.addOperatorInner("exportDef", 0, 0, 2,  new OperatorExportDef("exportDef"));
		this.addOperatorInner("new", 0, 0, -1, null);
		this.addOperatorInner("method", 0, 0, 2,true, null);
		this.addOperatorInner("field", 0, 0, 1, null);
		this.addOperatorInner("f", 0, 0, 1, null);
		this.addOperatorInner("selffunction", 0, 0, 1, null);
		this.addOperatorInner(".", 0, 0, -1, null);
		this.addOperatorInner("(", 0, 0, -1, new OperatorNullOp("("));
		this.addOperatorInner(")", 0, 0, -1, new OperatorNullOp(")"));

		this.addOperatorInner("{", 0, 0, -1, new OperatorNullOp("("));
		this.addOperatorInner("}", 0, 0, -1, new OperatorNullOp(")"));
		
		this.addOperatorInner("[", 0, 0, -1, null);
		this.addOperatorInner("]", 0, 0, -1, null);
		this.addOperatorInner("array", 1, 0, 2,true, new OperatorArray("array"));
		this.addOperatorInner("++", 1, 0, 1, null);
		this.addOperatorInner("--", 1, 0, 1, null);
		this.addOperatorInner("~", 1, 0, 1, null);
		this.addOperatorInner("in", 0, 0, 2, true,new OperatorIn("in"));
		this.addOperatorInner("cast", 0, 0, 2, new OperatorCast("cast"));
		this.addOperatorInner("!", 1, 0, 1, new OperatorNot("!"));
		this.addOperatorInner("not", 1, 0, 1, new OperatorNot("not"));
		this.addOperatorInner("*", 2, 0, 2, new OperatorMultiDiv("*"));
		this.addOperatorInner("/", 2, 0, 2, new OperatorMultiDiv("/"));
		this.addOperatorInner("%", 2, 0, 2, null);
		this.addOperatorInner("like", 2, 0, 2, new OperatorLike("like"));
		this.addOperatorInner("+", 3, 0, 2, new OperatorAddReduce("+"));
		this.addOperatorInner("-", 3, 0, 2, new OperatorAddReduce("-"));
		this.addOperatorInner("<<", 4, 0, 2, null);
		this.addOperatorInner(">>", 4, 0, 2, null);
		this.addOperatorInner(">>>", 4, 0, 2, null);
		this.addOperatorInner("<", 5, 0, 2, new OperatorEqualsLessMore("<"));
		this.addOperatorInner(">", 5, 0, 2, new OperatorEqualsLessMore(">"));
		this.addOperatorInner("<=", 5, 0, 2, new OperatorEqualsLessMore("<="));
		this.addOperatorInner(">=", 5, 0, 2, new OperatorEqualsLessMore(">="));
		this.addOperatorInner("==", 6, 0, 2, new OperatorEqualsLessMore("=="));
		this.addOperatorInner("!=", 6, 0, 2, new OperatorEqualsLessMore("!="));
		this.addOperatorInner("<>", 6, 0, 2, new OperatorEqualsLessMore("<>"));
		this.addOperatorInner("&", 7, 0, 2, null);
		this.addOperatorInner("^", 8, 0, 2, null);
		this.addOperatorInner("&&", 9, 0, 2, new OperatorAnd("&&"));
		this.addOperatorInner("and", 9, 0, 2, new OperatorAnd("and"));
		this.addOperatorInner("而且", 9, 0, 2, new OperatorAnd("而且"));
		this.addOperatorInner("||", 10, 0, 2, new OperatorOr("||"));
		this.addOperatorInner("or", 10, 0, 2, new OperatorOr("or"));
		this.addOperatorInner("或者", 10, 0, 2, new OperatorOr("或者"));
		this.addOperatorInner("=", 12, 1, 2, new OperatorEvaluate("="));
		this.addOperatorInner("for", 20, 0, 2, new OperatorFor("for"));
		this.addOperatorInner("if", 20, 0, -1, new OperatorIf("if"));
		this.addOperatorInner("then", 20, 0, -1, new OperatorNullOp("if"));
		this.addOperatorInner("else", 20, 0, -1, new OperatorNullOp("if"));
		
	 	this.addOperatorInner("exportAlias", 21, 0, 2,  new OperatorExportAlias("exportAlias"));
	 	this.addOperatorInner("alias", 21, 0, 2,  new OperatorAlias("alias"));
		this.addOperatorInner("call", 25, 0, 1, new OperatorCall("call"));
		this.addOperatorInner("break", 25, 0, 0, new OperatorBreak("break"));
		this.addOperatorInner("continue", 25, 0, 0, new OperatorContinue("continue"));
		
		//return 的操作数量是根据堆栈的实际情况来看的，999是一个特殊标志
		this.addOperatorInner("return", 26, 0, 999, new OperatorReturn("return"));
	 	this.addOperatorInner("macro", 30, 0, 2,  new OperatorMacro("macro"));
	 	this.addOperatorInner("function", 30, 0, 3,  new OperatorFunction("function"));

		this.addOperatorInner(",", 99, 0, -1, new OperatorNullOp(","));
		
		this.addOperatorInner(";", 100, 0, -1, new OperatorNullOp(";"));
		
		//---------------
		this.addFunction("max", new OperatorMinMax("max"));
		this.addFunction("min", new OperatorMinMax("min"));
		this.addFunction("round", new OperatorRound("round"));
     }
	/**
	 * 为已经存在的操作符定义别名，主要用于定义不同的错误信息输出
	 * @param aAliasName 操作符号别名
	 * @param name 原始操作符号别名
	 * @param errorInfo 当操作执行结果为false的时候输出的错误信息
	 * @throws Exception
	 */
	 public void addOperatorWithAlias(String aAliasName,String name,String errorInfo) throws Exception{
		 if (this.opStrList.containsKey(name) == false){
			 throw new Exception(name + " 不是系统级别的操作符号，不能设置别名");
		 }else{
			 
			 OpStr orgiOpStr  = this.opStrList.get(name);
			 OperatorBase orgiOperator = this.opMap.get(name);
			 if(orgiOperator == null){
				 throw new Exception(name + " 不能被设置别名");
			 }
			 Class opClass = orgiOperator.getClass();
			 Constructor<Operator> constructor = null;
			 try{
			   constructor =  opClass.getConstructor(String.class,String.class,String.class);
			 }catch(Exception e ){
				 throw new Exception(name + " 不能被设置别名:" + e.getMessage());
			 }
			 if(constructor == null){
				 throw new Exception(name + " 不能被设置别名");
			 }
			 
			 OpStr destOpStr  = new OpStr(aAliasName,name,orgiOpStr.PRI,orgiOpStr.Combine,orgiOpStr.OpDataMember,orgiOpStr.isNAddOneParameterCount);
			 OperatorBase destOperator = constructor.newInstance(aAliasName,name,errorInfo);
			 
	    	 if(this.opMap.containsKey(aAliasName)){
	    		 throw new RuntimeException("操作符号：\"" + aAliasName + "\" 已经存在");
	    	 }
			 this.opStrList.put(aAliasName,destOpStr);
	    	 this.opMap.put(aAliasName,destOperator);    
		 }		 
	 }

     /**
      * 增加用户的操作函数，例如group(1,2,3)
      * @param name 操作函数名称
      * @param op 自定的操作符号
      */
     public void addFunction(String name, OperatorBase op) {
    	 if(this.opMap.containsKey(name)){
    		 throw new RuntimeException("操作符号：\"" + name + "\" 已经存在");
    	 }
       op.setIsCanCache(true);
       this.m_function.add(name);
       this.opMap.put(name, op);
     }
     /**
      * 用户定义的操作符号优先级都设置为最高,操作数为两个
      * @param name 
      * @param op
      */
     public void addOperator(String name,Operator op){
     	this.addOperatorInner(name, 0, 0, 2, false, op);
     }
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
      if(errorInfo != null && errorInfo.trim().length() == 0){
    	  errorInfo = null;
      }	  
      this.addFunction(name, new SelfDefineClassFunctionOperator(name,aClassName,aFunctionName,aParameterTypes, errorInfo));
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
    if(errorInfo != null && errorInfo.trim().length() == 0){
      	  errorInfo = null;
        }	 	
     this.addFunction(name, new SelfDefineServiceFunctionOperator(name,aServiceObject,aFunctionName,aParameterTypes,errorInfo));
    }
    
    protected void addOperatorInner(String name,int pri,int combine,int aOpDataMember,OperatorBase op){
   	 this.addOperatorInner(name, pri, combine, aOpDataMember, false, op);
    }
    
    protected void addOperatorInner(String name,int pri,int combine,int aOpDataMember,boolean isNAddOneParameterCount,OperatorBase op){
   	 if(this.opMap.containsKey(name)){
   		 throw new RuntimeException("操作符号：\"" + name + "\"已经存在");  
   	 }
   	 this.opStrList.put(name,new OpStr(name,name,pri,combine,aOpDataMember,isNAddOneParameterCount));
   	 this.opMap.put(name, op);    	 
    }

    /**
      创建一个新的操作符实例
    */
    protected OperatorBase newInstance(ExpressItem opItem) throws Exception
    {
    	OperatorBase op = null;
      if(opItem instanceof ExpressItemField){
        op = new OperatorField(opItem.getAliasName(),((ExpressItemField)opItem).fieldName);
      }else if(opItem instanceof ExpressItemNew){
        op = new OperatorNew(opItem.getAliasName());
      }else if(opItem instanceof ExpressItemMethod){
        op = new OperatorMethod(opItem.getAliasName(),((ExpressItemMethod)opItem).methodName);
      }else {
        op =(OperatorBase)opMap.get(opItem.getAliasName());
      }
      if (op == null)
         throw new Exception("不支持的处理类型：" + opItem.getAliasName());
      return op;
    }
    /**
     获取操作符的优先级
     数字越小，优先级越高
    */
    protected int getPRI(String name)
    {
      if (m_function.contains(name) == true)
         name ="f";
      OpStr op = (OpStr)opStrList.get(name);
      if (op !=null)
         return op.PRI;
      else return -1;
    }
    protected boolean isNAddOneParameterCount(String name){
    	OpStr op = (OpStr)opStrList.get(name);
        if (op !=null)
           return op.isNAddOneParameterCount;
        else 
        	return false;
    }
    
    /**
     获取操作符的结合性
     //操作符结合性  0：从左到右；1：从右到左
    */
    protected int getCombine(String name)
    {

      if (m_function.contains(name) == true)
         name ="f";
      OpStr op = (OpStr)opStrList.get(name);
      if (op !=null)
         return op.Combine;
      else return -1;
    }
    protected boolean isOperator(String name)
    {
       if (m_function.contains(name) == true)
          return true;
       //对特殊的几个操作符号进行处理，避免在表达试中的变量名称，被当做操作符号处理了
       if (name.equals("f")||name.equals("cast")||name.equals("method")||name.equals("field"))
          return false;

       if (opStrList.get(name) != null)
          return true;
       return false;
    }
    /**
     * 根据别名获取真实的名称
     * @param name
     * @return
     */
    protected String getOperatorRealName(String name){
      OpStr op = opStrList.get(name);
      if(op != null){
    	  return op.Name;
      }else{
    	  return name;
      }
    }

    /**
     *
     *@return
     * 1---弹出栈顶数据运算；0---操作符入栈；2---弹出栈顶的'('抛弃；
     *  3---运算结束；-1---语法错误
     */
    protected int compareOp(String op1,String op2) throws Exception
    {
      int result = -1;
      if (op1.equalsIgnoreCase("if") &&  op2.equalsIgnoreCase("then")){
    	  result = 4; 
      }else if(op1.equalsIgnoreCase("if") &&  op2.equalsIgnoreCase("else")){
    	  result = 5; 
      }else if(op1.equals("{")&&(op2.equals("}"))){
          result =  6;
      }else if(op1.equals(";")&&(op2.equals(","))){
          result =  7;
      }else if (op1.equals("{")) //(比所有的操作符级别低
          result = 0;
      else if (op2.equals("{")) //(比所有的操作符级别低
          result = 0;
      else if (op2.equals("}")){ //）比所有的操作符级别高
          result = 1;    	  
      }else if(op1.equals("(")&&(op2.equals(")")))
          result =  2;
      else if (op1.equals("(")) //(比所有的操作符级别低
          result = 0;
      else if (op2.equals("(")) //(比所有的操作符级别低
          result = 0;
      else if (op2.equals(")")) //）比所有的操作符级别高
          result = 1;
      else if (op1.equals(";")&&(op2.equals(";")))
          result =  3;
      else if (this.getPRI(op1) <this.getPRI(op2))
        result =  1;
      else if(this.getPRI(op1) > this.getPRI(op2))
        result =  0;
      else if(this.getCombine(op1) == 0)
            result =  1;
      else
            result =  0;

     return result;
    }

    protected boolean isFunction(String name){
       for (int i=0;i< this.m_function.size();i++)
         if (this.m_function.contains(name) == true)
           return true;
       return false;
     }
   protected String getRealName(String name){
	   OperatorBase op = this.opMap.get(name);
	   if(op != null){
          return op.name;
	   }else{
	      return name;
	   }
   }
     public int getDataMember(String name){
       if (isFunction(name))
          return 1;
       OpStr op = (OpStr)opStrList.get(name);
       return op.OpDataMember;
     }

     /**
      * 进行对象比较
      * @param op1
      * @param op2
      * @return 0--等于 ，负数 < , 正数 >
      * @throws Exception
      */
     public  static int compareData(Object op1,Object op2) throws Exception{
      int compareResult = -1;

      if(op1 instanceof String)
         compareResult = ((String)op1).compareTo(op2.toString());
      else if(op1 instanceof Long){
      	if(op2 instanceof Number){
      		compareResult = ((Long)op1).compareTo(Long.valueOf(((Number)op2).longValue()));
      	}
      	else{
      	  compareResult = ((Long)op1).compareTo(Long.valueOf(op2.toString()));
      	}
      }
      else if(op1 instanceof Integer){
      	if(op2 instanceof Number){
      		compareResult = ((Integer)op1).compareTo(Integer.valueOf(((Number)op2).intValue()));
      	}
      	else{
      	  compareResult = ((Integer)op1).compareTo(Integer.valueOf(op2.toString()));
      	}
      }
      else if(op1 instanceof Double){
      	if(op2 instanceof Number){
      		compareResult = ((Double)op1).compareTo(Double.valueOf(((Number)op2).doubleValue()));
      	}
      	else{
      	  compareResult = ((Double)op1).compareTo(Double.valueOf(op2.toString()));
      	}
      }
      else if(op1 instanceof Float){
      	if(op2 instanceof Number){
      		compareResult = ((Float)op1).compareTo(new Float(((Number)op2).floatValue()));
      	}
      	else{
      	  compareResult = ((Float)op1).compareTo(Float.valueOf(op2.toString()));
      	}
      }
      else if ((op1 instanceof Boolean) && (op2 instanceof Boolean))
      {
          if (((Boolean)op1).booleanValue() ==((Boolean)op2).booleanValue())
             compareResult =0;
          else
             compareResult =-1;
       }
      else
         throw new Exception(op1 + "和" + op2 +"不能执行compare 操作");
      return compareResult;
    }

  }


 class OpStr
  { public String Name;   //操作符名称
    public String aliasName;//操作符号别名
    public int PRI;    //操作符优先级
    public int Combine;//操作符结合性  0：从左到右；1：从右到左
    public int OpDataMember; //操作数个数
    public boolean isNAddOneParameterCount = false;
   
    public OpStr(String aAliasName, String name,int pri,int combine,int aOpDataMember,boolean aIsNAddOneParameterCount){    	
        this.Name = name;
        this.aliasName = aAliasName;
        this.PRI = pri;
        this.Combine = combine;
        this.OpDataMember = aOpDataMember;
    	this.isNAddOneParameterCount = aIsNAddOneParameterCount;
    }
  }
