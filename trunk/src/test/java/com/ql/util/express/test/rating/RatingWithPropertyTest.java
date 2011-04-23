package com.ql.util.express.test.rating;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
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
		ExpressLoader loader = new ExpressLoader(runner);
		loader.loadExpressFromFile("ratingWithProperty");
		//����������
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("��������", logisticsOrder);
		context.put("���׶���", tcOrder);
		context.put("�ִ�����", goodsOrder);
		SubjectMananger subjectMananger = new SubjectMananger();
		context.put("����", subjectMananger);
		
		//��ȡ������ָ�
		InstructionSet[] sets = new InstructionSet[]{
				loader.getInstructionSet("ratingWithProperty")
		};
		//ִ��ָ��
		runner.execute(sets, loader, context, null, false,false,null);
		//����ֳɽ��
		System.out.println("----------�ֳɽ��----------------");
		for(Object item : subjectMananger.getSubjectValues()){
			System.out.println(item);
		}		
	}
}
