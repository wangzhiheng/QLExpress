package com.ql.util.express;

/**
 * ���ر��ʽ��Դ�ӿ�
 * @author xuannan
 *
 */
public interface IExpressResourceLoader {
	/**
	 * ���ݱ��ʽ���ƻ�ȡ���ʽ������
	 * @param expressName
	 * @return
	 * @throws Exception
	 */
	public String loadExpress(String expressName) throws Exception;
}
