package com.ql.util.express.instruction;

import java.lang.reflect.Array;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

class OperateDataArrayItem extends OperateDataAttr {
	OperateData arrayObject;
	int index;
	public OperateDataArrayItem(OperateData aArrayObject,int aIndex) {
		super("array[" + aArrayObject +"," + aIndex +"]",null);
		this.arrayObject = aArrayObject;
		this.index = aIndex;
	}
	public void toResource(StringBuilder builder,int level){		
		builder.append(this.index);
    }
	public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
		  return this.arrayObject.getObject(context).getClass();
	}
	public Object getObjectInner(InstructionSetContext<String,Object> context){
		try {
			return Array.get(this.arrayObject.getObject(context),this.index);
		} catch (Exception e) {
			 throw new RuntimeException(e);
		}
	}

	public void setObject(InstructionSetContext<String,Object> context, Object value) {
		try {
		 Array.set(this.arrayObject.getObject(context), this.index, value);
		} catch (Exception e) {
			 throw new RuntimeException(e);
		}
	}
}

class OperateDataField extends OperateDataAttr {
	Object fieldObject;
	String orgiFieldName;
	
	public OperateDataField(Object aFieldObject,String aFieldName) {
		super(null,null);
		this.name = aFieldObject.getClass().getName() + "." + aFieldName;
		this.fieldObject = aFieldObject;
		this.orgiFieldName =aFieldName;
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

    public Object transferFieldName(InstructionSetContext<String,Object> context,String oldName){
    	try{
    	   OperateDataAttr o = (OperateDataAttr)context.findAliasOrDefSymbol(oldName);
    	   if(o != null){
    		  return o.getObject(context);
    	   }else{
    	     return oldName;
    	   }
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}
    }
	public Object getObjectInner(InstructionSetContext<String,Object> context) {
		//如果能找到aFieldName的定义,则再次运算

		return ExpressUtil.getProperty(this.fieldObject,transferFieldName(context,this.orgiFieldName));
	}
    
	public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
		  return  this.type;
	}

	public void setObject(InstructionSetContext<String,Object> context, Object value) {
		ExpressUtil.setProperty(fieldObject, transferFieldName(context,this.orgiFieldName), value);
	}
}


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
	public Object getObjectInner(InstructionSetContext<String,Object> context) {
		try {
			return realAttr.getObject(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
    
	public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
		return realAttr.getType(context);
	}

	public void setObject(InstructionSetContext<String,Object> context, Object object) {		
		try {
			realAttr.setObject(context, object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

class OperateClass extends OperateData {
	private String name;
	private Class<?> m_class;
	public OperateClass(String name, Class<?> aClass) {
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
    public Class<?> getVarClass(){
    	return this.m_class;
    }
    public void reset(String aName,Class<?> newClass){
    	this.name = aName;
    	this.m_class = newClass;
    }
	public Object getObjectInner(InstructionSetContext<String,Object> parent) {
		return m_class;
	}
}

