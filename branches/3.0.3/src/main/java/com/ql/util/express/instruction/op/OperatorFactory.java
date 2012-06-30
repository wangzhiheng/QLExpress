package com.ql.util.express.instruction.op;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.parse.ExpressNode;

public class OperatorFactory {	
	  
	/**
	 * �Ƿ���Ҫ�߾��ȼ���
	 */
	protected boolean isPrecise = false;
	private Map<String, OperatorBase> operator = new HashMap<String, OperatorBase>();
	
	  public OperatorFactory(boolean aIsPrecise){
		  this.isPrecise = aIsPrecise;
		  addOperator("new",new OperatorNew("new"));
		  addOperator("anonymousNewArray",new OperatorAnonymousNewArray("anonymousNewArray"));
		  addOperator("NewList",new OperatorAnonymousNewList("NewList"));		  
		  addOperator(":",new OperatorKeyValue(":"));
		  addOperator("NewMap",new OperatorAnonymousNewMap("NewMap"));
		  addOperator("def",  new OperatorDef("def"));
		  addOperator("exportDef",  new OperatorExportDef("exportDef"));
		  addOperator("!",new OperatorNot("!"));
		  addOperator("*", new OperatorMultiDiv("*"));
		  addOperator("/", new OperatorMultiDiv("/"));
		  addOperator("%", new OperatorMultiDiv("%"));
		  addOperator("mod", new OperatorMultiDiv("mod"));
		  addOperator("+",new OperatorAddReduce("+"));
		  addOperator("-",new OperatorAddReduce("-"));
		  addOperator("<",new OperatorEqualsLessMore("<"));
		  addOperator(">",new OperatorEqualsLessMore(">"));
		  addOperator("<=",new OperatorEqualsLessMore("<="));
		  addOperator(">=",new OperatorEqualsLessMore(">="));
		  addOperator("==",new OperatorEqualsLessMore("=="));
		  addOperator("!=",new OperatorEqualsLessMore("!="));
		  addOperator("<>",new OperatorEqualsLessMore("<>"));
		  addOperator("&&",new OperatorAnd("&&"));
		  addOperator("||",new OperatorOr("||"));
		  addOperator("nor",new OperatorNor("nor"));
		  addOperator("=",new OperatorEvaluate("="));
		  addOperator("exportAlias",new OperatorExportAlias("exportAlias"));
		  addOperator("alias",new OperatorAlias("alias"));
		  addOperator("break",new OperatorBreak("break"));
		  addOperator("continue",new OperatorContinue("continue"));
		  addOperator("return",new OperatorReturn("return"));		  
		  addOperator("METHOD_CALL",new OperatorMethod());	 
		  addOperator("FIELD_CALL",new OperatorField()); 
		  addOperator("ARRAY_CALL",new OperatorArray("ARRAY_CALL"));
		  addOperator("++",new OperatorDoubleAddReduce("++"));
		  addOperator("--",new OperatorDoubleAddReduce("--"));
		  addOperator("cast", new OperatorCast("cast"));
		  addOperator("macro",new OperatorMacro("macro"));
		  addOperator("function",new OperatorFunction("function"));
		  addOperator("in", new OperatorIn("in"));	
		  addOperator("like", new OperatorLike("like"));
		}
	public void addOperator(String name, OperatorBase op) {
		OperatorBase oldOp = this.operator.get(name);
		if (oldOp != null) {
			throw new RuntimeException("�ظ������������" + name + "����1��"
					+ oldOp.getClass() + " ����2��" + op.getClass());
		}
		op.setPrecise(this.isPrecise);
		op.setAliasName(name);
		operator.put(name, op);
	}
	public OperatorBase replaceOperator(String name, OperatorBase op){
		OperatorBase old =  this.operator.remove(name);
		this.addOperator(name, op);
		return old;
	}

	@SuppressWarnings("unchecked")
	public void addOperatorWithAlias(String aAliasName,String name,String errorInfo) throws Exception{
		 if (this.operator.containsKey(name) == false){
			 throw new Exception(name + " ����ϵͳ����Ĳ������ţ��������ñ���");
		 }else{
			 OperatorBase orgiOperator = this.operator.get(name);
			 if(orgiOperator == null){
				 throw new Exception(name + " ���ܱ����ñ���");
			 }
			 OperatorBase destOperator = null;
			if (orgiOperator instanceof CanClone) {
				destOperator = ((CanClone)orgiOperator).cloneMe(aAliasName, errorInfo);
			} else {
				Class<OperatorBase> opClass = (Class<OperatorBase>) orgiOperator.getClass();
				Constructor<OperatorBase> constructor = null;
				try {
					constructor = (Constructor<OperatorBase>) opClass
							.getConstructor(String.class, String.class,String.class);
				} catch (Exception e) {
					throw new Exception(name + " ���ܱ����ñ���:" + e.getMessage());
				}
				if (constructor == null) {
					throw new Exception(name + " ���ܱ����ñ���");
				}
				destOperator = constructor.newInstance(aAliasName, name,errorInfo);
			}
	    	 if(this.operator.containsKey(aAliasName)){
	    		 throw new RuntimeException("�������ţ�\"" + aAliasName + "\" �Ѿ�����");
	    	 }
	    	 this.addOperator(aAliasName,destOperator);    
		 }		 
	 }
	public boolean isExistOperator(String operName) throws Exception {
		return operator.containsKey(operName);
	}
    public OperatorBase getOperator(String aOperName){
    	return this.operator.get(aOperName);
    }

	/**
	 * ����һ���µĲ�����ʵ��
	 */
	public OperatorBase newInstance(ExpressNode opItem) throws Exception {
		OperatorBase op = operator.get(opItem.getNodeType().getName());
		if (op == null) {
			op = operator.get(opItem.getTreeType().getName());
		}
		if(op == null){
			op = operator.get(opItem.getValue());
		}
		if (op == null)
			throw new Exception("û��Ϊ\"" + opItem.getValue() + "\"����������������");
		return op;
	}
	public OperatorBase newInstance(String opName) throws Exception {
		OperatorBase op = operator.get(opName);
		if (op == null){
			throw new Exception("û��Ϊ\"" + opName + "\"����������������");
		}	
		return op;		
	}
}
