package com.ql.util.express.match;

public class NodeTypeManagerTestImpl implements INodeTypeManager {

	public INodeType findNodeType(String name) {
		return new TestNodeTypeImpl(name);
	}

}

class TestNodeTypeImpl implements INodeType{
	String tag;
    public TestNodeTypeImpl(String aTag){
    	this.tag = aTag;
    }
	public String getTag() {
		return this.tag;
	}

	public INodeTypeManager getManager() {
		throw new RuntimeException("û��ʵ�ֵķ���");
	}

	@Override
	public QLPatternNode getPatternNode() {
		throw new RuntimeException("û��ʵ�ֵķ���");
	}

	@Override
	public INodeType isEqualsOrChildAndReturn(INodeType nodeType) {
		throw new RuntimeException("û��ʵ�ֵķ���");
	}
	
}