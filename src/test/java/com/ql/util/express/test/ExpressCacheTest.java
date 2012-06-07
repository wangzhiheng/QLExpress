package com.ql.util.express.test;

import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRemoteCacheRunner;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.LocalExpressCacheRunner;

/**
 * ����ExpressRunner�Ľű����������
 * @author tianqiao
 *
 */
public class ExpressCacheTest {
	
	ExpressRunner runner = new ExpressRunner();
	
	@Test
	public void testScriptCache() throws Exception {
		runner.addMacro("����ƽ���ɼ�", "(����+��ѧ+Ӣ��)/3.0");
		IExpressContext<String, Object> context =new DefaultContext<String, Object>();
		context.put("����", 88);
		context.put("��ѧ", 99);
		context.put("Ӣ��", 95);
		long times =10000;
		long start = new java.util.Date().getTime();
		while(times-->0){
			calulateTask(false, context);
		}
		long end = new java.util.Date().getTime();
		echo("���������ʱ��"+ (end-start) +" ms");
		
		times =10000;
		start = new java.util.Date().getTime();
		while(times-->0){
			calulateTask(true, context);
		}
		end = new java.util.Date().getTime();
		echo("�������ʱ��"+ (end-start) +" ms");
			
	}

	@Test
	public void testLocalCacheMutualImpact()throws Exception {
		
		//�����ڱ��صĽű�����ȫ�ֵģ������໥����
		
		runner.addMacro("����ƽ���ɼ�", "(����+��ѧ+Ӣ��)/3.0");
		runner.addMacro("�Ƿ�����", "����ƽ���ɼ�>90");
		IExpressContext<String, Object> context =new DefaultContext<String, Object>();
		context.put("����", 88);
		context.put("��ѧ", 99);
		context.put("Ӣ��", 95);
		echo(runner.execute("�Ƿ�����", context, null, false, false));
	}
	
	@Test
	public void testRemoteCache(){
		//���ݵ�Ԥ�ȼ���
		ExpressRunner runner =new ExpressRunner();		
		ExpressRemoteCacheRunner cacheRunner = new LocalExpressCacheRunner(runner);
		cacheRunner.loadCache("����ƽ���ɼ�", "(����+��ѧ+Ӣ��)/3.0");
		cacheRunner.loadCache("�Ƿ�����", "����ƽ���ɼ�>90");
		
		IExpressContext<String, Object> context =new DefaultContext<String, Object>();
		context.put("����", 88);
		context.put("��ѧ", 99);
		context.put("Ӣ��", 95);
		//ExpressRemoteCacheRunner��ֻ��ִ���Լ�ԭ�еĽű����ݣ������໥֮����룬��֤��ߵĽű���ȫ��
		echo(cacheRunner.execute("����ƽ���ɼ�", context, null, false, false, null));
		try{
			echo(cacheRunner.execute("����ƽ���ɼ�>90", context, null, false, false, null));			
		}catch(Exception e){
			echo("ExpressRemoteCacheRunnerֻ֧��Ԥ�ȼ��صĽű�����");
		}
		try{
			echo(cacheRunner.execute("�Ƿ�����", context, null, false, false, null));			
		}catch(Exception e){
			echo("ExpressRemoteCacheRunner��֧�ֽű�����໥����");
		}
	}
	
	private void echo(Object obj){
		System.out.println(obj);
	}
	
	private void calulateTask(boolean isCache, IExpressContext<String, Object> context) throws Exception{
		runner.execute("����ƽ���ɼ�", context, null, isCache, false);
	}
	
	

}
