package com.ql.util.express.instruction;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;


public class OperateDataAttr extends OperateData {
		protected String name;
		public OperateDataAttr(String aName,Class<?> aType) {
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
		public Object getObjectInner(InstructionSetContext<String,Object> context) {
			if (this.name.equalsIgnoreCase("null")) {
				return null;
			}
			if (context == null) {
				throw new RuntimeException("û�����ñ��ʽ����������ģ����ܻ�ȡ���ԣ�\"" + this.name
						+ "\"������ʽ");
			}
			try {
				   return context.get(this.name);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	    
		public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
			   if(this.type != null){
				   return this.type;
			   }
			   Object obj = context.get(name);
			   if (obj == null)
			     return null;
			   else
			     return obj.getClass();
		}

		public void setObject(InstructionSetContext<String,Object> parent, Object object) {
			try {
				  parent.put(this.name, object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}