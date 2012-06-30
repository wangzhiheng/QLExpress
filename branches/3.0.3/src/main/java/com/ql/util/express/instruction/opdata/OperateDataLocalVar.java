package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;



public class OperateDataLocalVar extends OperateDataAttr {
	public OperateDataLocalVar(String name,Class<?> type) {
		super(name,type);
	}
	public String toString() {
		try {			
			return name +":localVar";
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	public Object getObjectInner(InstructionSetContext<String,Object> context) {
		try {
			return this.dataObject;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
	public Class<?> getType(InstructionSetContext<String,Object> context) throws Exception {
		  return this.type;
	}

	public void setObject(InstructionSetContext<String,Object> parent, Object value) {
		this.dataObject = value;
	}
}
