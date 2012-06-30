package com.ql.util.express;


/**
 * 数据类型定义
 * @author qhlhl2010@gmail.com
 *
 */

public class OperateData implements java.io.Serializable {
	private static final long serialVersionUID = 4749348640699065036L;
	public final static transient OperateData OperateData_TRUE = new OperateData(true,Boolean.TYPE);
	public final static transient OperateData OperateData_FALSE = new OperateData(false,Boolean.TYPE);
	
	protected Object dataObject;
	public Class<?> type;

	public OperateData(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}
    public Class<?> getDefineType(){
    	throw new RuntimeException(this.getClass().getName() + "必须实现方法:getDefineType");
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
    public Object getObjectInner(InstructionSetContext<String,Object> context) throws Exception{
    	return this.dataObject;
    }
    public void setObject(InstructionSetContext<String,Object> parent, Object object) throws Exception {
		throw new RuntimeException("必须在子类中实现此方法");
	}
	public String toJavaCode(){		
		if(this.getClass().equals(OperateData.class) == false){
			throw new RuntimeException(this.getClass().getName() + "没有实现：toJavaCode()");
		}
		String result ="new " + OperateData.class.getName() +"(";
		if(String.class.equals(this.type)){
			result = result + "\"" + this.dataObject + "\"";
		}else if(this.type.isPrimitive()){
			result = result + this.dataObject.getClass().getName() +".valueOf(\"" + this.dataObject + "\")";
		}else{
			result = result + "new " + this.dataObject.getClass().getName() + "(\"" + this.dataObject.toString() + "\")";
		}
		result = result + "," + type.getName() + ".class";
		result = result + ")";
		return result;
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
