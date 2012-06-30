package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataKeyValue extends OperateData {
	OperateData key;
	OperateData value;

	public OperateDataKeyValue(OperateData aKey, OperateData aValue) {
		super(null, null);
		this.key = aKey;
		this.value = aValue;
	}

	public OperateData getKey() {
		return key;
	}

	public OperateData getValue() {
		return value;
	}

	public String toString() {
		return this.key + ":" + this.value;
	}

	public Object getObjectInner(InstructionSetContext<String, Object> context) {
		throw new RuntimeException("û��ʵ�ַ�����getObjectInner");
	}

	public Class<?> getType(InstructionSetContext<String, Object> context)
			throws Exception {
		throw new RuntimeException("û��ʵ�ַ�����getType");
	}

	public void setObject(InstructionSetContext<String, Object> parent,
			Object object) {
		throw new RuntimeException("û��ʵ�ַ�����setObject");
	}
}