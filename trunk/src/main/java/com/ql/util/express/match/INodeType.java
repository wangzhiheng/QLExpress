package com.ql.util.express.match;


/**
 * ∆•≈‰¿‡–Õ
 * @author xuannan
 *
 */
public interface INodeType {
	public String getTag();	
	public INodeTypeManager getManager();
	
	public QLPatternNode getPatternNode();
	public INodeType isEqualsOrChildAndReturn(INodeType nodeType);
}
