package com.ql.util.express.test.rating;

import java.util.Map;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.Operator;
/**
 * ��Ŀ��������
 * @author xuannan
 *
 */
class SujectOperator extends Operator {
	public SujectOperator(String aName) {
		this.name = aName;
	}
	public SujectOperator(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}

	@SuppressWarnings("rawtypes")
	public OperateData executeInner(InstructionSetContext<String,Object> context,
			OperateData[] list) throws Exception {
		if (list.length != 2) {
			throw new Exception("��Ŀ�����Ĳ��������������Ŀ����ID�Ϳ�Ŀ����");
		}
		Object userId =  list[0].getObject(context) ;
		Object subjectId =   list[1].getObject(context);
		if(userId == null || subjectId == null){
			throw new Exception("��Ŀ����ID�Ϳ�Ŀ���Ʋ���Ϊnull");
		}
		OperateData result = new OperateDataSubject((Map)context.get("���ÿ�Ŀ"),userId,subjectId);
		return result;
	}

	@Override
	public Object executeInner(Object[] list) throws Exception {
		 throw new Exception("����Ҫʵ�ֵķ���");
	}
}
@SuppressWarnings("rawtypes")
class OperateDataSubject extends OperateData{
	Object userId  ;
	Object subjectId ;
	Map container;

   public OperateDataSubject(Map aContainer,Object aUserId,Object aSubjectId){
	 super(null,Double.class);
	 this.userId = aUserId;
	 this.subjectId = aSubjectId;
	 this.container = aContainer;
   }
   public Object getObjectInner(InstructionSetContext<String,Object> context){
		String key = this.userId + "-" + this.subjectId;
		SubjectValue subject = (SubjectValue) container.get(key);
		if (subject == null) {
			return 0d;
		} else {
			return subject.value;
		}
   }
   @SuppressWarnings("unchecked")
public void setObject(InstructionSetContext<String,Object> parent, Object value) {
	   String key  = this.userId +"-" + this.subjectId;
	   SubjectValue subject = (SubjectValue)container.get(key);
	   if(subject == null){
		   subject = new SubjectValue();
		   subject.subjectId = this.subjectId;
		   subject.userId = this.userId;
		   container.put(key,subject);
	   }
	   subject.value = ((Number)value).doubleValue();
	}

}