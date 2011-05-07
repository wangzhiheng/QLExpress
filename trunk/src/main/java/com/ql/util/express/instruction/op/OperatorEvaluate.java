package com.ql.util.express.instruction.op;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperatorEvaluate extends OperatorBase {
	public OperatorEvaluate(String name) {
		this.name = name;
	}
	public OperatorEvaluate(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public OperateData executeInner(InstructionSetContext<String,Object> parent, OperateData[] list) throws Exception {
		return executeInner(parent, list[0], list[1]);
	}

	public OperateData executeInner(InstructionSetContext<String,Object> parent,
			OperateData op1, OperateData op2) throws Exception {
		Class<?> targetType = op1.getDefineType();
		Class<?> sourceType = op2.getType(parent);
		if (targetType != null) {
			if (ExpressUtil.isAssignable(targetType, sourceType) == false) {
				if (targetType.isArray()) {
					if (ExpressUtil.isAssignable(targetType.getComponentType(),
							sourceType) == false) {
						throw new Exception("��ֵʱ������ת������"
								+ ExpressUtil.getClassName(sourceType)
								+ " ����ת��Ϊ "
								+ ExpressUtil.getClassName(targetType
										.getComponentType()));
					} else {
						// ʲô������
					}
				} else {
					throw new Exception("��ֵʱ������ת������"
							+ ExpressUtil.getClassName(sourceType) + " ����ת��Ϊ "
							+ ExpressUtil.getClassName(targetType));
				}
			}

		}
		 Object result = op2.getObject(parent);
		 if(targetType != null){
			 result = ExpressUtil.castObject(result,targetType,false);
		 }
		 op1.setObject(parent,result);
		return op1;
	}

}