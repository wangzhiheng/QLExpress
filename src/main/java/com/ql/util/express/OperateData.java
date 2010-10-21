package com.ql.util.express;

import java.lang.reflect.Array;
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
    public void setObject(InstructionSetContext parent, Object object) {
		throw new RuntimeException("必须在子类中实现此方法");
	}
	public String toString() {
		if( this.dataObject == null)
			return this.type + ":null";
		else{
			return this.dataObject.toString();
		}
	}
	public void toResource(StringBuilder builder,int level){
		if(this.dataObject != null){
			builder.append(this.dataObject.toString());
		}else{
			builder.append("null");
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
    public void toResource(StringBuilder builder,int level){		
			builder.append(this.name);
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
		   if(this.type != null){
			   return this.type;
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
class OperateDataArrayItem extends OperateDataAttr {
	OperateDataAttr arrayObject;
	int index;
	public OperateDataArrayItem(OperateDataAttr aArrayObject,int aIndex) {
		super("array[" + aArrayObject.name +"," + aIndex +"]",null);
		this.arrayObject = aArrayObject;
		this.index = aIndex;
	}
	public void toResource(StringBuilder builder,int level){		
		builder.append(this.index);
    }
	public Class getType(InstructionSetContext context) throws Exception {
		  return this.arrayObject.getObject(context).getClass();
	}
	public Object getObjectInner(InstructionSetContext context){
		try {
			return Array.get(this.arrayObject.getObject(context),this.index);
		} catch (Exception e) {
			 throw new RuntimeException(e);
		}
	}

	public void setObject(InstructionSetContext context, Object value) {
		try {
		 Array.set(this.arrayObject.getObject(context), this.index, value);
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
class OperateClass extends OperateData {
	private String name;
	private Class m_class;
	public OperateClass(String name, Class aClass) {
		super(null,null);
		this.name = name;
		this.m_class = aClass;
	}
	public void toResource(StringBuilder builder,int level){		
		builder.append(this.name);
    }
	public String toString() {
		return "Class:" + name;
		// return name;
	}
    public Class getVarClass(){
    	return this.m_class;
    }
    public void reset(String aName,Class newClass){
    	this.name = aName;
    	this.m_class = newClass;
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
	public int getChildCount(){
		return this.op.getChildCount();
	}
	public void toResource(StringBuilder builder,int level){
		throw new RuntimeException("不应该执行此方法");
	}
	public String toResource(){
		throw new RuntimeException("不应该执行此方法");
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
	public int getChildCount();
	public void toResource(StringBuilder builder,int level);
	public String toResource();
}

abstract class ExpressTreeNodeImple implements ExpressTreeNode{
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
			return new ExpressTreeNode[0];
		}
		return (ExpressTreeNode[])children.toArray(new ExpressTreeNode[0]);
	}
	public void setChildren(ExpressTreeNode[] aChildren) {
		if(this.children == null){
			this.children = new ArrayList<ExpressTreeNode>();
		}else{
			this.children.clear();
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
	public int getChildCount(){
		if(this.children == null){
			return 0;
		}
		return this.children.size();
	}
    public int getMaxStackSize() {
		return maxStackSize;
	}
	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}
	public void toResourceOfChild(StringBuilder builder,int level) {
		if (this.children != null) {
			for (ExpressTreeNode child : this.children) {
				child.toResource(builder,level);
			}
		}
	}
	public String toResource(){
		StringBuilder builder = new StringBuilder();
		this.toResource(builder,0);
		return builder.toString();
	}
}
class ExpressTreeNodeRoot extends ExpressTreeNodeImple{
	String name;
	String startTag;
	String endTag;
	boolean isAddLevel;
	ExpressTreeNodeRoot(String aName,String aStartTag ,String aEndTag,boolean aIsAddLevel){
		this.name = aName;
		this.startTag = aStartTag;
		this.endTag = aEndTag;
		this.isAddLevel = aIsAddLevel;
	}
	public String toString(){
		return "ExpressNode[" + this.name +"]";
	}
	public void toResource(StringBuilder builder,int level){
		if(this.startTag != null){
		  builder.append(this.startTag);
		}
		
		this.toResourceOfChild(builder,level + (this.isAddLevel == true? 1:0));
		
		if(this.endTag!= null){
			  builder.append(this.startTag);
		}
	}
}
