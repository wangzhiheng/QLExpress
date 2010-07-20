package com.ql.util.express;

/**
 * 表达式计算的数据注入扩展接口，在99.9%的情况下用不着
 * @author qhlhl2010@gmail.com
 *
 */
@SuppressWarnings("unchecked")

public interface IExpressContextExtend extends IExpressContext {
  
  /**
   * 获取属性的Class,主要用于动态执行方法时候，能够根据参数类型准确的获取函数。
   * 例如：一个类中有 f(String name),f(Object name) 当需要通过属性类型精确的时候使用
   * 缺省的实现方式:
   * 	public Class getClassType(String name) throws Exception {
		Object obj = this.getAttrValue(name);
		if (obj == null)
			return null;
		return obj.getClass();
	   }
	}
   * @param name
   * @return
   * @throws Exception
   */

public Class getClassType(String name) throws Exception;
}
