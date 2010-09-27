package com.ql.util.express;

import java.util.ArrayList;
import java.util.List;





class ExpressItem extends ExpressTreeNodeImple {
  protected String name;
  protected String aliasName;
  protected int opDataNumber;
//  public int point = -1;
  
  
  public ExpressItem(String aName){
	   this(aName,aName);
  }
  public ExpressItem(String aAliasName, String aName){
    name = aName;
    this.aliasName = aAliasName;
    this.opDataNumber = 0;
  }
  public void toResource(StringBuilder builder,int level){		
		builder.append(this.aliasName);
  }
  public String toString()
  {
	   return this.aliasName;
  }
  public String getAliasName(){
	   return this.aliasName;
  }
}
class ExpressItemNew extends ExpressItem{
  public ExpressItemNew(){
    super("new");
    this.opDataNumber = 2;
  }
}

class ExpressItemField extends ExpressItem{
  protected String fieldName;
  public ExpressItemField(String aName,String aFieldName){
    super(aName);
    this.fieldName = aFieldName;
  }
  public void toResource(StringBuilder builder,int level){
	  this.toResourceOfChild(builder, level);
	  builder.append(".").append(this.fieldName);
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

class ExpressItemArray extends ExpressItem{
	  public ExpressItemArray(String name){
	    super(name);
	  }
	  public String toString()
	  {
	    return "Operator:" + name;
	  }
	}
class ExpressItemSelfDefineFunction extends ExpressItem{
	   protected String functionName;
	   public ExpressItemSelfDefineFunction(String aFunctionName){
	     super("selffunction");
	     this.functionName = aFunctionName;
	   }
	   public String toString()
	   {
	     return "SelfDefineFunction:" + name +":" + this.functionName + " OperandNumber:" + this.opDataNumber;
	   }

}

/**
 * 定义变量
 * @author xuannan
 *
 */
class ExpressItemDef extends ExpressItem{
	   protected String type;
	   protected String varName;	   
	   public ExpressItemDef(String aAliasName,String aName,String aType,String aVarName){
	     super(aAliasName,aName);
	     this.type = aType;
	     this.varName = aVarName;
	   }
	   public String toString()
	   {
	     return "Operator:" + this.aliasName +":" + this.type+ " name:" + this.varName;
	   }

	 }
@SuppressWarnings("unchecked")
class ExpressImport {

	protected List<String> m_packages = new ArrayList<String>();

	public ExpressImport() {
		this.resetPackages();
	}

	public void addPackage(String aPackageName) {
		int point = aPackageName.indexOf(".*");
        if(point >=0){
			aPackageName = aPackageName.substring(0, point);
		}
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
			String tmp ="";
			if(m_packages.get(i).endsWith("."+ name) == true){
				tmp =  m_packages.get(i);
			}else{
				tmp =  m_packages.get(i) + "." + name;					
			}
			try {
				result = Class.forName(tmp);
			} catch (ClassNotFoundException ex) {
				//不做任何操作
			}
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}

class ForRelBreakContinue{
	 ExpressItem node;
	 List<InstructionGoTo> breakList = new ArrayList<InstructionGoTo>();
	 List<InstructionGoTo> continueList = new ArrayList<InstructionGoTo>();
	 public ForRelBreakContinue(ExpressItem aNode){
		 node = aNode;
	 }
}

