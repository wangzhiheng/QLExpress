package com.ql.util.express;


/**
 * 数据类型定义
 * @author qhlhl2010@gmail.com
 *
 */

public class OperateData { // extends ExpressTreeNodeImple {
	protected Object dataObject;
	public Class<?> type;

	public OperateData(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}

	public Class<?> getType(InstructionSetContext<String,Object> parent) throws Exception {
		if (type != null)
			return type;

		Object obj = this.getObject(parent);
		if (obj == null)
			return null;
		else
			return obj.getClass();
	}

	public final Object getObject(InstructionSetContext<String,Object> context) throws Exception {
		return getObjectInner(context);
	}
    public Object getObjectInner(InstructionSetContext<String,Object> context){
    	return this.dataObject;
    }
    public void setObject(InstructionSetContext<String,Object> parent, Object object) {
		throw new RuntimeException("必须在子类中实现此方法");
	}
	public String toString() {
		if( this.dataObject == null)
			return this.type + ":null";
		else{
			if(this.dataObject instanceof Class){
				return ExpressUtil.getClassName((Class<?>)this.dataObject);
			}else{
			    return this.dataObject.toString();
			}
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
