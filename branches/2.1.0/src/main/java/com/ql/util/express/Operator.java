package com.ql.util.express;

import com.ql.util.express.instruction.OperatorBase;

/**
 * �������Ļ���
 * @author xuannan
 *
 */
public abstract class Operator extends  OperatorBase{
	
	public OperateData executeInner(InstructionSetContext<String,Object> context, OperateData[] list) throws Exception{
		Object[] parameters = new Object[list.length];
		for(int i = 0;i <list.length;i++){			
			parameters[i] = list[i].getObject(context);
		}
		Object result = this.executeInner(parameters);
		if(result != null && result.getClass().equals(OperateData.class)){
			throw new Exception("�������Ŷ���ķ������ʹ���" + this.getAliasName());
		}
		if(result == null){
			return new OperateData(null,null);
		}else{
			return new OperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
		}
	}
	public abstract Object executeInner(Object[] list) throws Exception;

    /**
     * ���ж���Ƚ�
     * @param op1
     * @param op2
     * @return 0--���� ������ < , ���� >
     * @throws Exception
     */
    public  static int compareData(Object op1,Object op2) throws Exception{
     int compareResult = -1;

     if(op1 instanceof String){
        compareResult = ((String)op1).compareTo(op2.toString());
     }else if(op2 instanceof String){
    	compareResult = op1.toString().compareTo((String)op2);
     }else if(op1 instanceof Number && op2 instanceof Number){
   	  //���ֱȽ�
   	  compareResult =  OperatorOfNumber.compareNumber((Number)op1, (Number)op2);
     }
     else if ((op1 instanceof Boolean) && (op2 instanceof Boolean))
     {
         if (((Boolean)op1).booleanValue() ==((Boolean)op2).booleanValue())
            compareResult =0;
         else
            compareResult =-1;
      }
     else
        throw new Exception(op1 + "��" + op2 +"����ִ��compare ����");
     return compareResult;
   }

}
