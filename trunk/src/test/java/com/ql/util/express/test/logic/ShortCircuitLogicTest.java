package com.ql.util.express.test.logic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

/**
 * ��·�߼�������
 * @author tianqiao
 *
 */
public class ShortCircuitLogicTest {
	
	private ExpressRunner runner = new ExpressRunner();
	
	public void initial() throws Exception{
		runner.addOperatorWithAlias("С��","<","$1 С�� $2 ����������");
		runner.addOperatorWithAlias("����",">","$1 ���� $2 ����������");
	}
	
	public boolean calculateLogicTest(String expression,IExpressContext<String,Object> expressContext,List<String> errorInfo) throws Exception {			
        Boolean result = (Boolean)runner.execute(expression, expressContext, errorInfo, true, false);
        if(result.booleanValue() == true){
        	return true;
        }
        return false;
	}	
	
	/**
	 * ���ԷǶ�·�߼�,�������������Ϣ
	 * @throws Exception
	 */
	@Test
	public void testShortCircuit() throws Exception {
		runner.setShortCircuit(true);
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();	
		expressContext.put("Υ������", 100);
		expressContext.put("��ٽ��׿۷�", 11);
		expressContext.put("VIP", false);
		List<String> errorInfo = new ArrayList<String>();
		initial();
		String expression ="2 С�� 1 and (Υ������ С�� 90 or ��ٽ��׿۷� С�� 12)";
		boolean result = calculateLogicTest(expression, expressContext, errorInfo);
		if(result){
			System.out.println("result is success!");
		}else{
			System.out.println("result is fail!");
			for(String error : errorInfo){
				System.out.println(error);
			}
		}
		
	}
	
	/**
	 * ���ԷǶ�·�߼�,�������������Ϣ
	 * @throws Exception
	 */
	@Test
	public void testNoShortCircuit() throws Exception {
		runner.setShortCircuit(false);
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();	
		expressContext.put("Υ������", 100);
		expressContext.put("��ٽ��׿۷�", 11);
		expressContext.put("VIP", false);
		List<String> errorInfo = new ArrayList<String>();
		initial();
		String expression ="2 С�� 1 and (Υ������ С�� 90 or ��ٽ��׿۷� С�� 12)";
		boolean result = calculateLogicTest(expression, expressContext, errorInfo);
		if(result){
			System.out.println("result is success!");
		}else{
			System.out.println("result is fail!");
			for(String error : errorInfo){
				System.out.println(error);
			}
		}
		
	}

}
