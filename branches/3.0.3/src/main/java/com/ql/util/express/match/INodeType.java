package com.ql.util.express.match;


/**
 * ƥ������
 * @author xuannan
 *
 */
public interface INodeType {
	public String getName();	
	public INodeTypeManager getManager();
	public QLPatternNode getPatternNode();
}
