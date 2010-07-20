package com.ql.util.express;

/**
 * 数据类型定义
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")
public class OperateData {
	private Object dataObject;

	private Class type;
    public int point = -1;
	public OperateData(Object obj, Class aType) {
		this.type = aType;
		this.dataObject = obj;
	}

	public Class getType(IExpressContext parent) throws Exception {
		if (type != null)
			return type;

		Object obj = this.getObject(parent);
		if (obj == null)
			return null;
		else
			return obj.getClass();
	}

	public final Object getObject(IExpressContext context) throws Exception {
		return getObjectInner(context);
	}
    public Object getObjectInner(IExpressContext context){
    	return this.dataObject;
    }

	public String toString() {
		return dataObject.toString();
	}

}

@SuppressWarnings("unchecked")
class OperateDataAttr extends OperateData {
	String name;

	public OperateDataAttr(String name) {
		super(null,null);
		this.name = name;
	}

	public String toString() {
		try {
			return name;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	public Object getObjectInner(IExpressContext context) {
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
    
	public Class getType(IExpressContext context) throws Exception {
		   if (context!= null 
				   && context instanceof IExpressContextExtend){
			   return ((IExpressContextExtend)context).getClassType(name);
		   }	   
		   Object obj = context.get(name);
		   if (obj == null)
		     return null;
		   else
		     return obj.getClass();
	}

	public void setObject(IExpressContext parent, Object object) {
		try {
			parent.put(this.name, object);
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

	public Object getObjectInner(IExpressContext parent) {
		return m_class;
	}

}