package com.ql.util.express;

/**
 * ���ʽ���������ע��ӿ�
 * @author qhlhl2010@gmail.com
 *
 */
public interface IExpressContext<K,V> {
/**
 * �������ƴ������б�����ȡ����ֵ��������ʽ���õ���Spring�Ķ���Ҳ��ͨ���˷�����ȡ
 * @param name ��������
 * @return
 * @throws Exception
 */
	public V get(Object key);
 /**
  * ���ʽ����Ľ���������ûص���ϵͳ������  userId = 3 + 4
  * @param name ��������
  * @param object ����ֵ
  * @throws Exception
  */
	public V put(K name, V object);
}
