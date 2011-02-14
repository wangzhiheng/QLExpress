/**
 *
 * <p>Title:Operator </p>
 * <p>Description:���ʽ��������� </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author ǽ��
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
 * ���ʽ���������
 * 
 * @author qhlhl2010@gmail.com

 * ����֪��   a love b = ? �� ���������ⶨ���Լ��Ĳ��������� �� ��ʹ��QLExpress���߰�
 * 
 * ������ʽ��Ա�ļ��㹤�ߣ��ŵ���Ҫ�����ڣ�
      A������ҪԤ�ȼ��ؿ�����Ҫ����������ֵ
      B�� �û����Ը���ҵ����Ҫ�Զ���������źͺ��� 
      C������ͬ������жϴ�����Ϣ�����������ҵ��ϵͳ�ڹ����жϵ�ʹ�ó����µ��û����顣����ҵ��ϵͳ��صĴ�����롣
   
       ��Ҫ��;��һЩҵ����������жϣ�ͬʱ��Ҫ�����صĴ�����Ϣ

 * ���Hello������
 * 
 *		String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
 *		ExpressRunner runner = new ExpressRunner();
 *		Object result = runner.execute(express, null, false, null);
 *		System.out.println("���ʽ���㣺" + express + " = " + result);  
 */
@SuppressWarnings("unchecked")
public class ExpressRunner
{

  private static final Log log = LogFactory.getLog(ExpressRunner.class);
  private boolean isTrace = false;
  private Map<String,InstructionSet> expressInstructionSetCache = new ConcurrentHashMap<String, InstructionSet>();
  
  protected OperatorManager m_operatorManager =  new OperatorManager();
  protected Map<Object,Object> m_cacheOracleParseString = new HashMap<Object,Object>();

	public ExpressRunner() {
	}

	public ExpressRunner(boolean aIsTrace) {
		this.isTrace = aIsTrace;
	}

  /**
	 * Ϊ�Ѿ����ڵĲ����������������Ҫ���ڶ��岻ͬ�Ĵ�����Ϣ���������  addOperatorWithAlias("����","in","�û������б���")
	 * 
	 * @param aAliasName
	 *            �������ű���
	 * @param name
	 *            ԭʼ�������ű���
	 * @param errorInfo
	 *            ������ִ�н��Ϊfalse��ʱ������Ĵ�����Ϣ
	 * @throws Exception
	 */
	public void addOperatorWithAlias(String aAliasName, String name,
			String errorInfo) throws Exception {
		this.m_operatorManager
				.addOperatorWithAlias(aAliasName, name, errorInfo);
	}
    /**
     * �û�����Ĳ����������ȼ�������Ϊ���,������Ϊ����
     * @param name 
     * @param op
     */
    public void addOperator(String name,Operator op){
    	this.m_operatorManager.addOperator(name,op);
    }
    /**
     * �����û��Ĳ�������������group,��������
     * addFunction("group",new GroupOperator("group"))
     * ����ʽ �� group(2,3,4) ִ�н��   9
     * �����ඨ�����£�
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
     * @param name ������������
     * @param op �Զ��Ĳ�������
     */
    public void addFunction(String name, Operator op) {
    	this.m_operatorManager.addFunction(name, op);
    };
    
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
    	this.m_operatorManager.addFunctionOfClassMethod(name, aClassName, aFunctionName, aParameterTypes, errorInfo);
    }
    
    /**
     * ���һ����ĺ������壬���磺Math.abs(double) ӳ��Ϊ���ʽ�е� "ȡ����ֵ(-5.0)"
     * @param name ��������
     * @param aClassName ������
     * @param aFunctionName ���еķ�������
     * @param aParameterClassTypes �����Ĳ�����������
     * @param errorInfo �������ִ�еĽ����false����Ҫ����Ĵ�����Ϣ
     * @throws Exception
     */
	public void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class<?>[] aParameterClassTypes, String errorInfo)
			throws Exception {
		this.m_operatorManager.addFunctionOfClassMethod(name, aClassName,
				aFunctionName, aParameterClassTypes, errorInfo);
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
    	this.m_operatorManager.addFunctionOfServiceMethod(name, aServiceObject, aFunctionName, aParameterTypes, errorInfo);
    }

	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, Class<?>[] aParameterClassTypes, String errorInfo)
			throws Exception {
		this.m_operatorManager.addFunctionOfServiceMethod(name, aServiceObject,
				aFunctionName, aParameterClassTypes, errorInfo);
	}

  protected Object[] getOpObjectList(String[] tmpList)  throws Exception
  {
      
	ExpressImport  tmpImportPackage = new ExpressImport();  
    List<Object>  list = new ArrayList<Object>();
    int point=0;
    
    //�ȴ���import��import��������ļ����ʼ�������ԣ�����
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
      }else if (this.m_operatorManager.isOperator(name)) { //�ж��Ƿ������
      	if(name.equals("-")
      			&& (list.size() ==0 || list.size()>0 && list.get(list.size() -1 ) instanceof ExpressItem)
      	    && (point <tmpList.length -1) 
      	    && ( tmpList[point + 1].charAt(0) >='0'
      	    	   && tmpList[point + 1].charAt(0) <='9'
      	    	  ||tmpList[point + 1].charAt(0)=='.')
      	    && (point == 0 || this.m_operatorManager.isOperator(tmpList[point -1]) == true && tmpList[point -1].equals(")")==false )	){ //�Ը����������⴦��
      		tmpList[point + 1] = '-' + tmpList[point + 1];
      		point = point + 1;
      		
      	}else{
          list.add(new ExpressItem(name,this.m_operatorManager.getOperatorRealName(name)));
          point = point + 1;
      	}
      }else if (name.charAt(0) == ':') {
    	throw new Exception("���ʽ���Ѿ�ȥ���Բ�����֧��");  
       // list.add(new OperateDataParameter(name.substring(1)));
       // point = point + 1;
      }else if (name.charAt(0) == '\'' || name.charAt(0) == '"') {
        list.add(new OperateData(name.substring(1, name.length() - 1), String.class));
        point = point + 1;
      } else if ( (name.charAt(0) >= '0' && name.charAt(0) <= '9') ||
               (name.charAt(0) == '.') || name.charAt(0)=='-') { //����       	
       if (name.endsWith("L") || name.endsWith("l")) {//long
         list.add(new OperateData(new Long(name.substring(0,name.length() - 1)), Long.TYPE));
       }else if (name.endsWith("f") || name.endsWith("F")) {//long
           list.add(new OperateData(new Float(name.substring(0,name.length() - 1)), Float.TYPE));
       }else if (name.endsWith("d") || name.endsWith("D")) {//long
             list.add(new OperateData(new Double(name.substring(0,name.length() - 1)), Double.TYPE));
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
        	//������������
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
          //�������Ͳ���(Integer);
          //ǰ����������������
          if(list.size() >0 && point < tmpList.length && list.get(list.size() -1) instanceof ExpressItem
             && ((ExpressItem)list.get(list.size() -1)).name.equals("(")==true
             && tmpList[point].equals(")")==true){
            //�Ƴ�ǰһ������������
            list.remove(list.size() -1 );
            list.add(new OperateClass(tmpStr,tmpClass));
            list.add(new ExpressItem("cast"));
            point = point + 1;//���ڴ����һ��������
          }else if( ( list.size() ==0
        		     || (list.get(list.size() -1)  instanceof ExpressItem == false)
        		     ||    this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("def") == false
        		        && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("new") == false
        		        && this.m_operatorManager.getRealName(((ExpressItem)list.get(list.size() -1)).name).equalsIgnoreCase("exportDef") == false
                    )
        		  && point < tmpList.length  && tmpList[point].equals("(") == false && 
        		  tmpList[point].equals(")") == false && tmpList[point].equals(".") == false ){//���� int a
        	//���� int a��def int a�� a=1; int b��new String( �������
        	list.add(new ExpressItem("def"));
        	list.add(new OperateClass(tmpStr,tmpClass));  
          }else{//�������Ͳ���
            list.add(new OperateClass(tmpStr,tmpClass));
          }
        }else{
        	//�� def��alias ����ĵ�һ�����������ַ�������
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
   * ͨ��"{","}",";"�ѳ����ֿ�
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
		  if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase(";")){//����һ���µ����
			    ExpressTreeNode temp = new ExpressTreeNodeRoot(";","",";\n",false);
			    nodeStack.peek().addChild(temp);
			  for(Object item:stackList.peek().toArray()){
				  temp.addChild((ExpressTreeNode)item);
			  }
			  stackList.peek().clear();
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("{")){//����һ���µ����
			  nodeStack.push(new ExpressTreeNodeRoot("{}","{\n","}\n",true));
			  stackList.push(new ArrayList<Object>());
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("}")){//����һ���µ����
			  //���� } ǰ��û�� ;��Ҳ��Ϊһ����ɵ���䴦��
			  if(false == (list[i - 1] instanceof ExpressItem == true &&  ((ExpressItem)list[i - 1]).name.equalsIgnoreCase(";")))
			  {   ExpressTreeNode temp = new ExpressTreeNodeRoot(";","",";\n",false);
			      nodeStack.peek().addChild(temp);
			      for(Object item:stackList.peek().toArray()){
			    	  temp.addChild((ExpressTreeNode)item);
			      }
		      }
			  //���� }
			  stackList.peek().clear();
			  stackList.pop();
			  stackList.peek().add(nodeStack.pop());
			  //��Ӧ{},���ǰ�治��if(true)then{}else{},for(){},������һ���µ����
			  if(i + 1 < list.length ){
				  List tmpList = stackList.peek();
				  if((list[i+1] instanceof ExpressItem  == true )
						&& ( tmpList.size() <=2
							 || tmpList.get(tmpList.size() - 3) instanceof ExpressItem == false
							 ||((ExpressItem)(tmpList.get(tmpList.size() - 3))).name.equalsIgnoreCase("for") == false
						   ) 	 
						&& (((ExpressItem)list[i+1]).name.equalsIgnoreCase(";")==false
						  || ((ExpressItem)list[i+1]).name.equalsIgnoreCase("else")==false 
						  ) ){
					  //ʲô������
				  }else{
					  list[i] = new ExpressItem(";");
					  i = i -1;
				  }
			  }
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("[")){//����һ���µ����
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
		  }else if(list[i] instanceof ExpressItem &&  ((ExpressItem)list[i]).name.equalsIgnoreCase("(")){//����һ���µ����
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
		     log.debug("----------�����-------------");
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
	  //����һ�仰
	  tmpList[tmpList.length - 1] = new ExpressItem(";");
	  root.setChildren(this.getCResultOne(tmpList));	  
	  return root;
  }
  
  protected ExpressTreeNode[] getCResultOne(Object[] list) throws Exception
  {
	try{
    if (list == null){
      throw new Exception("���ʽ����Ϊ��");
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
            case (2):// ( �� ) ����
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
            case(4):// if �� then
            	op1.opDataNumber = 2;
                i++;
            	break;
            case(5):// if �� else 
            	if(list[i -1] instanceof ExpressItem &&  ((ExpressItem)list[i -1]).name.equalsIgnoreCase("then") == true){
            		 //��if () then else ������� ����һ�� void ����������������Ĵ����ж�
            	   sdata.push(new OperateData(null,void.class));            	     
            	}
            	op1.opDataNumber = 3;
                i++;
            	break;
            case (3):
						if (sop.size() > 1){
							throw new Exception("���ʽ���ô������麯�������Ƿ�ƥ��,�ڲ�����ջ�л���δ����Ĳ�����");
						}
						if( sdata.size() > 1) {
							throw new Exception("���ʽ���ô������麯�������Ƿ�ƥ��,�ڲ�����ջ�ж���һ��������");
						}
						if (sdata.size() > 0) {//��break��continue�������û������
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
               sop.pop();//�׳���ջ���Ĳ�������
               if (op1.name.equals(",")){
            	    throw new Exception("���ʽ���ô���"); 
               }else{
                   // Ϊ���жϱ��ʽ���﷨�Ƿ���ȷ����Ҫ�Բ�����ջ���д�����֧��(2,3,4)ִ�н��Ϊ4����ʽ     
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
                  	 //ȷ���������ŵĲ�����
                  	 ExpressTreeNode tmpNode = (ExpressTreeNode)sdata.pop();
                  	 if(tmpNode instanceof MyPlace){
                  		 tmpNode = ((MyPlace)tmpNode).op;
                  	 }
                  	 tmpNode.setParent(op1);
                     tmpList.add(0,tmpNode);
                   }
                   op1.opDataNumber = opDataNumber;//���¼�����()�еĲ�����
                   op1.setChildren(tmpList.toArray(new ExpressTreeNode[0]));
                   
                   //����жϿ��ܴ���Ǳ������
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
		  throw new Exception("���ʽ�﷨��������[" + e.getMessage() +"] ���飺[" + getPrintInfo2(list," ")+"]",e);
	  }
	throw new Exception("û���ܹ�������ȷ�Ľ������ʽ");
    
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
	    	throw new Exception("For�������");
	    }
	}  
	protected boolean createInstructionSetPrivate(InstructionSet result,Stack<ForRelBreakContinue> forStack,ExpressTreeNode node,boolean isRoot)throws Exception {
		boolean returnVal = false;
		if(node instanceof OperateDataAttr){
			FunctionInstructionSet functionSet = result.getMacroDefine(((OperateDataAttr) node).getName());
			if(functionSet != null){//�Ǻ궨��
				result.insertInstruction(result.getCurrentPoint()+1, new InstructionCallMacro(((OperateDataAttr) node).getName()));
			}else{
			  result.addLoadAttrInstruction((OperateDataAttr)node,node.getMaxStackSize());	
			  if(node.getChildren().length >0){
				  throw new Exception("���ʽ���ô���");
			  }
			}  
		}else if(node instanceof OperateData){			  	
		  result.addLoadDataInstruction((OperateData)node,node.getMaxStackSize());	
		  if(node.getChildren().length > 0){
			  throw new Exception("���ʽ���ô���");
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
				//�����";"�������ţ�����Ҫ���������ջ�Ĳ���
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
					//��Ӷ���ı�������
					result.addExportDef(new ExportItem(children[1].toString(),ExportItem.TYPE_DEF,ExpressUtil.getClassName(((OperateClass)children[0]).getVarClass())));
				}else if(op instanceof OperatorExportAlias ){
					result.addExportDef(new ExportItem(children[0].toString(),ExportItem.TYPE_ALIAS,children[1].toResource()));
				}
			}
		}else{
			throw new Exception("��֧�ֵ���������:" + node.getClass());
		}
		return returnVal;
	}
	/**
	 * ������ָ��
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
	 * ���� if ��ָ���
	 * @param result
	 * @param node
	 * @throws Exception
	 */
	protected boolean createInstructionSetForIf(InstructionSet result,Stack<ForRelBreakContinue>  forStack,ExpressItem node)throws Exception {
    	ExpressTreeNode[] children = node.getChildren();
    	if(children.length < 2){
    		throw new Exception("if ������������Ҫ2�������� " );
    	}else if (children.length == 2) {
    		//����һ����֧
    		ExpressTreeNode[] oldChilder =  children;
    		children = new ExpressTreeNode[3];
    		children[0] = oldChilder[0];
    		children[1] = oldChilder[1];
    		children[2] = new OperateData(null,void.class);    			
    	}else if(children.length > 3){
    		throw new Exception("if ���������ֻ��3�������� " );
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
    		throw new Exception("funciton ��������Ҫ3�������� " );
    	}
    	InstructionSet functionSet = new InstructionSet(InstructionSet.TYPE_FUNCTION);
    	for(ExpressTreeNode item : children[1].getChildren()){
    		ExpressItem expressItem = (ExpressItem)item;
    		if(expressItem.name.equals("def") == false){
    			throw new Exception(expressItem +" ����һ�����ر�������");
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
    		throw new Exception("if ������������Ҫ2�������� " );
    	}else if(node.getChildren().length > 2){
    		throw new Exception("if ���������ֻ��2�������� " );
    	}
    	if(node.getChildren()[0].getChildren()!= null && node.getChildren()[0].getChildren().length > 3){
    		throw new Exception("ѭ���������ò�����:" + node.getChildren()[0]);	
    	}
    	//����������ʼָ��
	    result.insertInstruction(result.getCurrentPoint()+1, new InstructionOpenNewArea());			
	    forStack.push(new ForRelBreakContinue(node));
	    
    	//����������䲿��ָ��
    	ExpressTreeNode conditionNode = node.getChildren()[0];
    	int nodePoint = 0;
    	if (conditionNode.getChildren() != null && conditionNode.getChildren().length == 3){//�������壬�жϣ�����������
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[0],false);
    		nodePoint = nodePoint + 1;
    	}
    	//ѭ���Ŀ�ʼ��λ��
    	int loopStartPoint = result.getCurrentPoint()+ 1;
    	
    	//���������
    	InstructionGoToWithCondition conditionInstruction=null;
    	if(conditionNode.getChildren() != null 
    		&& (conditionNode.getChildren().length == 1
    			|| conditionNode.getChildren().length == 2 
    			|| conditionNode.getChildren().length == 3)
    		)	{    		
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    		//��ת��λ����Ҫ���ݺ�����ָ���������    		
    		conditionInstruction = new InstructionGoToWithCondition(false,-1,true);
    		result.insertInstruction(result.getCurrentPoint()+1,conditionInstruction);   
    		nodePoint = nodePoint+ 1;
    	}
    	int conditionPoint = result.getCurrentPoint();
    	//����ѭ����Ĵ���
    	createInstructionSetPrivate(result,forStack,node.getChildren()[1],false);
    	
    	int selfAddPoint = result.getCurrentPoint()+1;
    	//������������ָ��
    	if(conditionNode.getChildren()!= null &&(
    			conditionNode.getChildren().length == 2 || conditionNode.getChildren().length == 3
    			)){
    		createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    	}
    	//����һ����������ת
    	InstructionGoTo reStartGoto = new InstructionGoTo(loopStartPoint - (result.getCurrentPoint() + 1));
    	result.insertInstruction(result.getCurrentPoint()+1,reStartGoto); 
    	
    	//�޸������жϵ���תλ��
    	if(conditionInstruction != null){
    	   conditionInstruction.offset = result.getCurrentPoint() - conditionPoint + 1;
    	}
    	
    	//�޸�Break��Continueָ�����תλ��,ѭ������
    	ForRelBreakContinue rel =  forStack.pop();
    	for(InstructionGoTo item:rel.breakList){
    		item.offset = result.getCurrentPoint() -  item.offset ;
    	}
    	for(InstructionGoTo item:rel.continueList){
    		item.offset = selfAddPoint -  item.offset - 1;
    	}    	
    	
    	//�������������ָ��
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
			if(list[i] != null){
			   buffer.append(list[i]+"[" + list[i].getClass().getName()  + "]");
			}else{
				buffer.append(list[i]+"[NULL]");
			}
		}
		return buffer.toString();
	  }

  protected ExpressTreeNodeRoot parseCResult(String condition)throws Exception{

		String[] tmpList = parse(condition);
	    if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("ִ�еı��ʽ��" + condition);	    	
	    	log.debug("���ʷֽ���:" + getPrintInfo(tmpList,","));
	    }
	    Object[] tmpObjectList = getOpObjectList(tmpList);
	    if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("�﷨�ֽ���:" + getPrintInfo(tmpObjectList,","));
	    }
	    ExpressTreeNodeRoot result = getCResult(tmpObjectList);
	    return result;
}
  public InstructionSet parseInstructionSet(String condition)throws Exception{
	  InstructionSet result = createInstructionSet(this.parseCResult(condition),InstructionSet.TYPE_MAIN);
	  if(this.isTrace && log.isDebugEnabled()){
	    	log.debug("���ɵ�ָ�:\n" + result);
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
	 return  InstructionSet.executeOuter(instructionSets,loader,context, errorList, aFunctionCacheMananger, isTrace,isCatchException,aLog);
  }

  /**
   * �������
   */
  public void clearExpressCache(){
	  this.expressInstructionSetCache.clear();
  }
  
  /**
   * ִ��Ԥ�����ı��ʽ
   * @param context ���������Ҫ�������ģ����û�����ԣ�������null
   * @param list Ԥ�����ı��ʽ
   * @param errorList ���������Ϣ��List
   * @return ����Ľ��
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
    	throw new Exception("���ʽ����Ϊ��");
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

         //����׼ȷ�Ĳ���������
         int opDataNumber =m_operatorManager.getDataMember(opItem.name);
         if (opDataNumber <0){
           opDataNumber = opItem.opDataNumber;
         }
         opItem.opDataNumber = opDataNumber;         
         
         List<ExpressTreeNode> tmpList = new ArrayList<ExpressTreeNode>();         
         for(int index = opDataNumber - 1;index>=0;index--){
        	 //ȷ���������ŵĲ�����
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
     while (opStr.length() >0){ //����������ִ�
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
    if (c >= '0' && c <= '9') { //����
      return true;
    }
    else {
      return false;
    }
  }
  /**
   * �ı�������������.����Ϊ�������Ŵ���
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
    	//�����ַ����еġ�����
        while(index >0 && str.charAt(index - 1) =='\\'){
        	index = str.indexOf(c,index + 1);
        }
        if (index < 0)
        	throw new Exception("�ַ���û�йر�");
        //�Ƚ�������������У�����������
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
            || (c > 127))  //��׼�ַ�
       {
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(tmpOpStr,list);
           tmpOpStr = "";
       }else if(c=='.' && this.isNumber(tmpWord) == true){
           //�����֣������ݴ���
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(tmpOpStr,list);
           tmpOpStr = "";
       }else //���������ָ��
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
