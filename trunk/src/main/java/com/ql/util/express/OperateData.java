package com.ql.util.express;

/**
 * 数据类型定义
 * @author qhlhl2010@gmail.com
 *
 */



@SuppressWarnings("unchecked")
public class OperateData implements ExpressTreeNode {
	private Object dataObject;

	private Class type;
    private ExpressTreeNode parent;
    private ExpressTreeNode[] children;
	/**
	 * 堆栈的最大深度
	 */
    public int maxStackSize = 1; 
	

	public int point = -1;
    
	public OperateData(Object obj, Class aType) {
		this.type = aType;
		this.dataObject = obj;
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
    protected Object getObjectInner(IExpressContext context){
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
		   
		   if(context == null){
			   return null;
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
}

interface ExpressTreeNode{
	public void setParent(ExpressTreeNode parent);
	public ExpressTreeNode getParent();	
	
	public void setChildren(ExpressTreeNode[] children);
	public ExpressTreeNode[] getChildren();
	
	public void setMaxStackSize(int size);
	public int getMaxStackSize();
	
}