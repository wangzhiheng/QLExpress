package com.ql.util.express;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据类型定义
 * @author qhlhl2010@gmail.com
 *
 */



@SuppressWarnings("unchecked")
public class OperateData extends ExpressTreeNodeImple {
	protected Object dataObject;
	protected Class type;

	public OperateData(Object obj, Class aType) {
		this.type = aType;
		this.dataObject = obj;
	}

	public Class getType(InstructionSetContext parent) throws Exception {
		if (type != null)
			return type;

		Object obj = this.getObject(parent);
		if (obj == null)
			return null;
		else
			return obj.getClass();
	}

	public final Object getObject(InstructionSetContext context) throws Exception {
		return getObjectInner(context);
	}
    protected Object getObjectInner(InstructionSetContext context){
    	return this.dataObject;
    }

	public String toString() {
		if( this.dataObject == null)
			return this.type + ":null";
		else{
			return this.dataObject.toString();
		}
	}

}

@SuppressWarnings("unchecked")
class OperateDataAttr extends OperateData {
	String name;
	public OperateDataAttr(String aName,Class aType) {
		super(null,aType);
		this.name = aName;
	}
	public OperateDataAttr(String name) {
		super(null,null);
		this.name = name;
	}
    public String getName(){
    	return name;
    }
	public String toString() {
		try {
			String str ="";
			if(this.type == null){
				str =  name;
			}else{
				str = name + "[" + this.type + "]"  ;
			}
			return str;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	public Object getObjectInner(InstructionSetContext context) {
		if (this.name.equalsIgnoreCase("null")) {
			return null;
		}
		if (context == null) {
			throw new RuntimeException("没有设置表达式计算的上下文，不能获取属性：\"" + this.name
					+ "\"请检查表达式");
		}
		try {
			   return context.get(this.name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
	public Class getType(InstructionSetContext context) throws Exception {
		   if (context!= null 
				   && context instanceof IExpressContextExtend){
			   return ((IExpressContextExtend)context).getClassType(name);
		   }
		   
		   if(context == null){
			   return null;
		   }
		   Object obj = context.get(name);
		   if (obj == null)
		     return null;
		   else
		     return obj.getClass();
	}

	public void setObject(InstructionSetContext parent, Object object) {
		try {
			  parent.put(this.name, object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
@SuppressWarnings("unchecked")
class OperateDataField extends OperateDataAttr {
	Object fieldObject;
	String fieldName;
	
	public OperateDataField(Object aFieldObject,String aFieldName) {
		super(null,ExpressUtil.getPropertyType(aFieldObject, aFieldName));
		this.name = aFieldObject.getClass().getName() + "." + aFieldName;
		this.fieldObject = aFieldObject;
		this.fieldName =aFieldName;
	}
	
    public String getName(){
    	return name;
    }
	public String toString() {
		try {			
			return name;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}


	public Object getObjectInner(InstructionSetContext context) {
			return ExpressUtil.getProperty(this.fieldObject, this.fieldName);
	}
    
	public Class getType(InstructionSetContext context) throws Exception {
		  return  this.type;
	}

	public void setObject(InstructionSetContext parent, Object value) {
		ExpressUtil.setProperty(fieldObject, fieldName, value);
	}
}

@SuppressWarnings("unchecked")
class OperateDataLocalVar extends OperateDataAttr {
	public OperateDataLocalVar(String name,Class type) {
		super(name,type);
	}
	public String toString() {
		try {			
			return name +":localVar";
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	public Object getObjectInner(InstructionSetContext context) {
		try {
			return this.dataObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
	public Class getType(InstructionSetContext context) throws Exception {
		  return this.type;
	}

	public void setObject(InstructionSetContext parent, Object value) {
		this.dataObject = value;
	}
}
@SuppressWarnings("unchecked")
class OperateDataAlias extends OperateDataAttr {
	OperateDataAttr realAttr;
	public OperateDataAlias(String aName,OperateDataAttr aRealAttr) {
		super(aName,null);
		this.realAttr = aRealAttr;
	}
	public String toString() {
		try {
			return this.name + "[alias=" + this.realAttr.name+"]";
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}
	public Object getObjectInner(InstructionSetContext context) {
		try {
			return realAttr.getObject(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
    
	public Class getType(InstructionSetContext context) throws Exception {
		return realAttr.getType(context);
	}

	public void setObject(InstructionSetContext context, Object object) {		
		try {
			realAttr.setObject(context, object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
@SuppressWarnings("unchecked")
class OperatorClass extends OperateData {
	private String name;
	private Class m_class;

	public OperatorClass(String name, Class aClass) {
		super(null,null);
		this.name = name;
		this.m_class = aClass;
	}

	public String toString() {
		return "Class:" + name;
		// return name;
	}

	public Object getObjectInner(InstructionSetContext parent) {
		return m_class;
	}
}

/**
 * 占位符号
 **/
class MyPlace implements ExpressTreeNode{
	ExpressItem op;
    public int maxStackSize = 1; 

    public int getMaxStackSize() {
		return maxStackSize;
	}
	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}
    
	public MyPlace(ExpressItem item){
		this.op = item;
	}
	public ExpressTreeNode getParent() {
		return op.getParent();
	}

	public void setParent(ExpressTreeNode aParent) {
		this.op.setParent(aParent);
	}
	
	public String toString(){
		return "MyPlace:" + op.toString();
	}
	public ExpressTreeNode[] getChildren() {
		return this.op.getChildren();
	}
	public void setChildren(ExpressTreeNode[] children) {
		this.op.setChildren(children);
	}
	public void addChild(ExpressTreeNode child){
		this.op.addChild(child);
	}
}

interface ExpressTreeNode{
	public ExpressTreeNode getParent();
	public void setParent(ExpressTreeNode aParent);
	public ExpressTreeNode[] getChildren() ;
	public void setChildren(ExpressTreeNode[] children);
    public int getMaxStackSize();
	public void setMaxStackSize(int maxStackSize);
	public void addChild(ExpressTreeNode child);
}

class ExpressTreeNodeRoot extends ExpressTreeNodeImple{
	String name;
	ExpressTreeNodeRoot(String aName){
		this.name = aName;
	}
	public String toString(){
		return "ExpressNode[" + this.name +"]";
	}
}
class ExpressTreeNodeImple implements ExpressTreeNode{
	private ExpressTreeNode parent;
	private List<ExpressTreeNode> children=null;
	/**
	 * 堆栈的最大深度
	 */
    public int maxStackSize = 1; 
	

	public ExpressTreeNode getParent() {
		return parent;
	}

	public void setParent(ExpressTreeNode aParent) {
		this.parent = aParent;
	}
	public ExpressTreeNode[] getChildren() {
		if(this.children == null){
			return null;
		}
		return (ExpressTreeNode[])children.toArray(new ExpressTreeNode[0]);
	}
	public void setChildren(ExpressTreeNode[] aChildren) {
		if(this.children == null){
			this.children = new ArrayList<ExpressTreeNode>();
		}
		if(aChildren != null){
			for(ExpressTreeNode node : aChildren){
				this.children.add(node);
			}
		}
		
	}
	public void addChild(ExpressTreeNode child){
		if(this.children == null){
			this.children = new ArrayList<ExpressTreeNode>();
		}
		this.children.add(child);
	}
    public int getMaxStackSize() {
		return maxStackSize;
	}
	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}
	
}