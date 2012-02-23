package com.ql.util.express.test.rating;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
/**
 * �ֳ����÷���
 * @author xuannan
 *
 */
public class RatingTest {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRating( ) throws Exception {
		Map logisticsOrder = new HashMap();
		Map tcOrder = new HashMap();
		Map goodsOrder = new HashMap();
		Map subjectValue = new HashMap();
		//��������������Ϣ
		logisticsOrder.put("����",4);
		logisticsOrder.put("�ִ�TP","����");
		logisticsOrder.put("����TP","����");
		logisticsOrder.put("��װTP","ǧ��");
        //����������
		ExpressRunner runner = new ExpressRunner();
		//�����Զ��庯��
		runner.addFunction("���ÿ�Ŀ",new SujectOperator("���ÿ�Ŀ"));
        //װ�طֳɹ���rating.ql�ļ�
		runner.loadExpress("rating");
		//����������
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("��������", logisticsOrder);
		context.put("���׶���", tcOrder);
		context.put("�ִ�����", goodsOrder);
		context.put("���ÿ�Ŀ", subjectValue);
		//ִ��ָ��
		runner.executeByExpressName("rating",context, null, false,false,null);
		
		//����ֳɽ��
		System.out.println("----------�ֳɽ��----------------");
		for(Object item : subjectValue.values()){
			System.out.println(item);
		}		
	}
}
