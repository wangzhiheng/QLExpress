package com.ql.util.express.test.rating;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
/**
 * �ֳ����÷���,ͨ����̬������ʵ��
 * @author xuannan
 *
 */
public class RatingWithPropertyTest {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRating( ) throws Exception {
		Map logisticsOrder = new HashMap();
		Map tcOrder = new HashMap();
		Map goodsOrder = new HashMap();
		//��������������Ϣ
		logisticsOrder.put("����",4);
		logisticsOrder.put("�ִ�TPID","����");
		logisticsOrder.put("����TPID","����");
		logisticsOrder.put("��װTPID","ǧ��");
        //����������
		ExpressRunner runner = new ExpressRunner();
		//�����Զ��庯��
		runner.addFunction("���ÿ�Ŀ",new SujectOperator("���ÿ�Ŀ"));
        //װ�طֳɹ���rating.ql�ļ�
		runner.loadExpress("ratingWithProperty");
		//����������
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("��������", logisticsOrder);
		context.put("���׶���", tcOrder);
		context.put("�ִ�����", goodsOrder);
		SubjectMananger subjectMananger = new SubjectMananger();
		context.put("����", subjectMananger);
		
		runner.executeByExpressName("ratingWithProperty", context, null, false,false,null);
		//����ֳɽ��
		System.out.println("----------�ֳɽ��----------------");
		for(Object item : subjectMananger.getSubjectValues()){
			System.out.println(item);
		}		
	}
}
