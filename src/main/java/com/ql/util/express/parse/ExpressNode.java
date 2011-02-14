package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;


public class ExpressNode{
	/**
	 * 节点类型
	 */
	private NodeType nodeType;
	
	private NodeType treeType;
	/**
	 * 节点值
	 */
	private String value;
	
	/**
	 * 节点原始值
	 */
	private String orgiValue;
	
	private Object objectValue;
	/**
	 * 父节点
	 */
	private ExpressNode parent;
	private List<ExpressNode> leftChildren;
	private List<ExpressNode> rightChildren;
	private boolean isSplitStatement = false;
	
	//private int line;
	//private int position;
	
	public ExpressNode(NodeType aType,String aValue) throws Exception{
		this(aType, aValue, null,null,null);
	}
	public ExpressNode(NodeType aType,String aValue,String aOrgiValue,Object aObjectValue,NodeType aTreeType) throws Exception{
		if(aType == null){
			throw new Exception(aValue + " 没有找到对应的节点类型");
		}
		this.nodeType = aType;
		this.treeType = aTreeType;
		if(aValue != null && aValue.length() >0){
		  this.value = aValue;
		}
		if(aOrgiValue != null && aOrgiValue.length() >0){
			  this.orgiValue = aOrgiValue;
		}
		if(aObjectValue != null){
			this.objectValue = aObjectValue;
		}
	}
	
	public NodeType isEqualsOrChildAndReturn(NodeType parent){
	    NodeType result = this.getTreeType().isEqualsOrChildAndReturn(parent);
	    if(result == null && this.treeType != null){
	 		result = this.getNodeType().isEqualsOrChildAndReturn(parent);
	 	}
	 	return result;
	}
	public boolean isTypeEqualsOrChild(String parent){
	 	boolean result = this.getTreeType().isEqualsOrChild(parent);
	 	if(result == false && this.treeType != null){
	 		result = this.getNodeType().isEqualsOrChild(parent);
	 	}
	 	return result;
	}
	public boolean isTypeEqualsOrChild(NodeType parent){
	 	boolean result = this.getTreeType().isEqualsOrChild(parent);
	 	if(result == false && this.treeType != null){
	 		result = this.getNodeType().isEqualsOrChild(parent);
	 	}
	 	return result;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType type) {
		this.nodeType = type;
	}
	public String getValue() {
		if(value == null){
			return this.nodeType.getTag();
		}else{
		  return value;
		}
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isSplitStatement() {
		return isSplitStatement;
	}
	public void setSplitStatement(boolean isSplitStatement) {
		this.isSplitStatement = isSplitStatement;
	}
	public String getOperatorName(){
		if(this.treeType != null && this.treeType.getOperatorName() != null){
			return this.treeType.getOperatorName();
		}
		if(this.nodeType.getOperatorName() != null){
			return this.nodeType.getOperatorName();
		}
		throw new RuntimeException("没有定义节点的操作信息：" + this.nodeType.getTag()+ (this.treeType == null?"":" 或者 "  +this.treeType.getTag()) );
	}
	public String getInstructionFactory(){
		if(this.nodeType.getInstructionFactory() != null){
			return this.nodeType.getInstructionFactory();
		}
		if(this.treeType != null && this.treeType.getInstructionFactory() != null){
			return this.treeType.getInstructionFactory();
		}
		throw new RuntimeException("没有定义节点的指令InstructionFactory信息：" + this.nodeType.getTag()+ (this.treeType == null?"":" 或者 "  +this.treeType.getTag()) );
	}
	
	public String getOrgiValue() {
		return orgiValue;
	}
	public void setOrgiValue(String orgiValue) {
		this.orgiValue = orgiValue;
	}
	public Object getObjectValue() {
		return objectValue;
	}
	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}
	public ExpressNode getParent() {
		return parent;
	}
	public void setParent(ExpressNode parent) {
		this.parent = parent;
	}
	
	public NodeType getRealTreeType(){
		return this.treeType;
	}
	public NodeType getTreeType() {
		if(this.treeType == null){
			return this.nodeType;
		}else{
		    return treeType;
		}
	}
	public void setTreeType(NodeType treeType) {
		this.treeType = treeType;
	}
	
	public List<ExpressNode> getLeftChildren() {
		return leftChildren;
	}
	public void setLeftChildren(List<ExpressNode> leftChildren) {
		this.leftChildren = leftChildren;
	}
	public List<ExpressNode> getRightChildren() {
		return rightChildren;
	}
	public void setRightChildren(List<ExpressNode> rightChildren) {
		this.rightChildren = rightChildren;
	}
	public void addLeftChild(ExpressNode leftChild){
		if(leftChild == null){
			return ;
		}
		if(this.leftChildren ==null){
			this.leftChildren = new ArrayList<ExpressNode>();
		}
		this.leftChildren.add(leftChild);
	}
	
	public void addRightChild(ExpressNode rightChild){
		if(rightChild == null){
			return ;
		}
		if(this.leftChildren ==null){
			this.leftChildren = new ArrayList<ExpressNode>();
		}
		this.leftChildren.add(rightChild);
	}
	
	public ExpressNode[] getChildren(){
		List<ExpressNode> result = new ArrayList<ExpressNode>();
		if(this.leftChildren != null && this.leftChildren.size() >0){
			result.addAll(this.leftChildren);
		}
		if(this.rightChildren != null && this.rightChildren.size() >0){
			result.addAll(this.rightChildren);
		}
		return result.toArray(new ExpressNode[0]);
	}
	
	public String toString(){
		  return (this.orgiValue == null ? this.getValue():this.orgiValue) + (this.nodeType.getTag() == null?"":(":" + this.nodeType.getTag()));
	}
}