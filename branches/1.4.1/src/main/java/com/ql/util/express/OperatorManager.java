package com.ql.util.express;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ������������
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
final class OperatorManager {

	protected List<String> m_function = new ArrayList<String>();
	protected Map<String, OperatorBase> opMap = new HashMap<String, OperatorBase>();

  /**
    ���ȼ���ָͬһʽ���ж���������ִ�кʹ���
    ͬһ����Ĳ�����������ͬ�����ȼ���
    ��ͬ���ȼ��ò������������ǣ����ݽ���Ծ����������
    ����Ӹߵ��͵����ȼ��г����������
    ͬһ���е���������ȼ���ͬ

    ע�⣺�ڽ���SQL�﷨����ʱ = ��ʾ �߼�������� ���ȼ�Ϊ 6 �����Ϊ������
          �ڽ���JAVA�﷨����ʱ = ��ʾ ��ֵ ���ȼ�Ϊ 12 �����Ϊ���ҵ���
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
		this.addOperatorInner("%", 2, 0, 2, new OperatorMultiDiv("%"));
		this.addOperatorInner("mod", 2, 0, 2, new OperatorMultiDiv("mod"));
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
		this.addOperatorInner("����", 9, 0, 2, new OperatorAnd("����"));
		this.addOperatorInner("||", 10, 0, 2, new OperatorOr("||"));
		this.addOperatorInner("or", 10, 0, 2, new OperatorOr("or"));
		this.addOperatorInner("����", 10, 0, 2, new OperatorOr("����"));
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
		
		//return �Ĳ��������Ǹ��ݶ�ջ��ʵ����������ģ�999��һ�������־
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
	 * Ϊ�Ѿ����ڵĲ����������������Ҫ���ڶ��岻ͬ�Ĵ�����Ϣ���
	 * @param aAliasName �������ű���
	 * @param name ԭʼ�������ű���
	 * @param errorInfo ������ִ�н��Ϊfalse��ʱ������Ĵ�����Ϣ
	 * @throws Exception
	 */
	 public void addOperatorWithAlias(String aAliasName,String name,String errorInfo) throws Exception{
		 if (this.opStrList.containsKey(name) == false){
			 throw new Exception(name + " ����ϵͳ����Ĳ������ţ��������ñ���");
		 }else{
			 
			 OpStr orgiOpStr  = this.opStrList.get(name);
			 OperatorBase orgiOperator = this.opMap.get(name);
			 if(orgiOperator == null){
				 throw new Exception(name + " ���ܱ����ñ���");
			 }
			 Class opClass = orgiOperator.getClass();
			 Constructor<Operator> constructor = null;
			 try{
			   constructor =  opClass.getConstructor(String.class,String.class,String.class);
			 }catch(Exception e ){
				 throw new Exception(name + " ���ܱ����ñ���:" + e.getMessage());
			 }
			 if(constructor == null){
				 throw new Exception(name + " ���ܱ����ñ���");
			 }
			 
			 OpStr destOpStr  = new OpStr(aAliasName,name,orgiOpStr.PRI,orgiOpStr.Combine,orgiOpStr.OpDataMember,orgiOpStr.isNAddOneParameterCount);
			 OperatorBase destOperator = constructor.newInstance(aAliasName,name,errorInfo);
			 
	    	 if(this.opMap.containsKey(aAliasName)){
	    		 throw new RuntimeException("�������ţ�\"" + aAliasName + "\" �Ѿ�����");
	    	 }
			 this.opStrList.put(aAliasName,destOpStr);
	    	 this.opMap.put(aAliasName,destOperator);    
		 }		 
	 }

     /**
      * �����û��Ĳ�������������group(1,2,3)
      * @param name ������������
      * @param op �Զ��Ĳ�������
      */
     public void addFunction(String name, OperatorBase op) {
    	 if(this.opMap.containsKey(name)){
    		 throw new RuntimeException("�������ţ�\"" + name + "\" �Ѿ�����");
    	 }
       op.setIsCanCache(true);
       this.m_function.add(name);
       this.opMap.put(name, op);
     }
     /**
      * �û�����Ĳ����������ȼ�������Ϊ���,������Ϊ����
      * @param name 
      * @param op
      */
     public void addOperator(String name,Operator op){
     	this.addOperatorInner(name, 0, 0, 2, false, op);
     }
     /**
      * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
      * @param name ��������
      * @param aClassName ������
      * @param aFunctionName ���еķ�������
      * @param aParameterTypes �����Ĳ�����������
      * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
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
      * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
      * @param name ��������
      * @param aClassName ������
      * @param aFunctionName ���еķ�������
      * @param aParameterClassTypes �����Ĳ�������Class
      * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
      * @throws Exception
      */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class[] aParameterClassTypes, String errorInfo)
			throws Exception {
		if (errorInfo != null && errorInfo.trim().length() == 0) {
			errorInfo = null;
		}
		this.addFunction(name, new SelfDefineClassFunctionOperator(name,
				aClassName, aFunctionName, aParameterClassTypes, errorInfo));
	}
     
    /**
     * ���ڽ�һ���û��Լ�����Ķ���(����Spring����)����ת��Ϊһ�����ʽ����ĺ���
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

	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, Class<?>[] aParameterClassTypes, String errorInfo)
			throws Exception {
		if (errorInfo != null && errorInfo.trim().length() == 0) {
			errorInfo = null;
		}
		this.addFunction(name, new SelfDefineServiceFunctionOperator(name,
				aServiceObject, aFunctionName, aParameterClassTypes, errorInfo));
	}
    
    protected void addOperatorInner(String name,int pri,int combine,int aOpDataMember,OperatorBase op){
   	 this.addOperatorInner(name, pri, combine, aOpDataMember, false, op);
    }
    
    protected void addOperatorInner(String name,int pri,int combine,int aOpDataMember,boolean isNAddOneParameterCount,OperatorBase op){
   	 if(this.opMap.containsKey(name)){
   		 throw new RuntimeException("�������ţ�\"" + name + "\"�Ѿ�����");  
   	 }
   	 this.opStrList.put(name,new OpStr(name,name,pri,combine,aOpDataMember,isNAddOneParameterCount));
   	 this.opMap.put(name, op);    	 
    }

    /**
      ����һ���µĲ�����ʵ��
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
         throw new Exception("��֧�ֵĴ������ͣ�" + opItem.getAliasName());
      return op;
    }
    /**
     ��ȡ�����������ȼ�
     ����ԽС�����ȼ�Խ��
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
     ��ȡ�������Ľ����
     //�����������  0�������ң�1�����ҵ���
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
       //������ļ����������Ž��д��������ڱ�����еı������ƣ��������������Ŵ�����
       if (name.equals("f")||name.equals("cast")||name.equals("method")||name.equals("field"))
          return false;

       if (opStrList.get(name) != null)
          return true;
       return false;
    }
    /**
     * ���ݱ�����ȡ��ʵ������
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
     * 1---����ջ���������㣻0---��������ջ��2---����ջ����'('������
     *  3---���������-1---�﷨����
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
      }else if (op1.equals("{")) //(�����еĲ����������
          result = 0;
      else if (op2.equals("{")) //(�����еĲ����������
          result = 0;
      else if (op2.equals("}")){ //�������еĲ����������
          result = 1;    	  
      }else if(op1.equals("(")&&(op2.equals(")")))
          result =  2;
      else if (op1.equals("(")) //(�����еĲ����������
          result = 0;
      else if (op2.equals("(")) //(�����еĲ����������
          result = 0;
      else if (op2.equals(")")) //�������еĲ����������
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
      * ���ж���Ƚ�
      * @param op1
      * @param op2
      * @return 0--���� ������ < , ���� >
      * @throws Exception
      */
     public  static int compareData(Object op1,Object op2) throws Exception{
      int compareResult = -1;

      if(op1 instanceof String){
         compareResult = ((String)op1).compareTo(op2.toString());
      }else if(op1 instanceof Number && op2 instanceof Number){
    	  //���ֱȽ�
    	  compareResult =  OperatorOfNumber.compareNumber((Number)op1, (Number)op2);
      }
      else if ((op1 instanceof Boolean) && (op2 instanceof Boolean))
      {
          if (((Boolean)op1).booleanValue() ==((Boolean)op2).booleanValue())
             compareResult =0;
          else
             compareResult =-1;
       }
      else
         throw new Exception(op1 + "��" + op2 +"����ִ��compare ����");
      return compareResult;
    }

  }


 class OpStr
  { public String Name;   //����������
    public String aliasName;//�������ű���
    public int PRI;    //���������ȼ�
    public int Combine;//�����������  0�������ң�1�����ҵ���
    public int OpDataMember; //����������
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
