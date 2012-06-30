package com.ql.util.express;


/**
 * �������Ͷ���
 * @author qhlhl2010@gmail.com
 *
 */

public class OperateData implements java.io.Serializable {
	private static final long serialVersionUID = 4749348640699065036L;	
	protected Object dataObject;
	protected Class<?> type;

	public OperateData(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}
	/**
	 * �����󻺴�ӿ�ʹ��
	 * @param obj
	 * @param aType
	 */
	protected void initial(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}
	protected void clear(){
		this.dataObject = null;
		this.type = null;
	}
    public Class<?> getDefineType(){
    	throw new RuntimeException(this.getClass().getName() + "����ʵ�ַ���:getDefineType");
    }
    public Class<?> getOrgiType(){
    	return this.type;
    }
	public Class<?> getType(InstructionSetContext parent) throws Exception {
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
    public Object getObjectInner(InstructionSetContext context) throws Exception{
    	return this.dataObject;
    }
    public void setObject(InstructionSetContext parent, Object object) throws Exception {
		throw new RuntimeException("������������ʵ�ִ˷���");
	}
	public String toJavaCode(){		
		if(this.getClass().equals(OperateData.class) == false){
			throw new RuntimeException(this.getClass().getName() + "û��ʵ�֣�toJavaCode()");
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
