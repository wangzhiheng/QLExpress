package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QLPattern {

	private static final Log log = LogFactory.getLog(QLPattern.class);
	
	public static QLPatternNode createPattern(INodeTypeManager nodeTypeManager,String pattern) throws Exception{
		return new QLPatternNode(nodeTypeManager,pattern);		
	}
	public static QLMatchResult findMatchStatement(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		QLMatchResult result = findMatchStatementWithAddRoot(aManager,pattern,nodes,point,true);
		if(result != null && result.matchs.size() != 1){
			throw new Exception("语法定义错误，必须有一个根节点：" + pattern);
		}
		return result;
	}
	private  static QLMatchResult findMatchStatementWithAddRoot(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point,boolean isRoot) throws Exception{
		QLMatchResult result = null;
		List<QLMatchResultTree> tempList = new ArrayList<QLMatchResultTree>();
		int count = 0;
		int lastPoint = point;
		while(true){
			QLMatchResult tempResult = null;
			if (pattern.matchMode == MatchMode.DETAIL) {
				tempResult = matchDetailOneTime(aManager,pattern,nodes, lastPoint);
			}else if (pattern.matchMode == MatchMode.AND) {
				tempResult = matchAndOneTime(aManager,pattern,nodes, lastPoint);
			}else if (pattern.matchMode == MatchMode.OR) {
				tempResult = matchOrOneTime(aManager,pattern,nodes, lastPoint);
			}else{
				throw new Exception("不正确的类型：" + pattern.matchMode.toString());
			}
			if(tempResult == null){
				if(count >= pattern.minMatchNum && count <=pattern.maxMatchNum){
					//正确匹配
					result = new QLMatchResult(tempList,lastPoint);
				}else{
					result = null;
				}
				break;
			}else{
				lastPoint = tempResult.matchLastIndex;
				tempList.addAll(tempResult.matchs);
			}
			count = count + 1;			
			if(count == pattern.maxMatchNum){
				result = new QLMatchResult(tempList,lastPoint);
				break;
			}
		}
		if(result != null && result.matchs.size() >0 && pattern.rootNodeType != null){
			QLMatchResultTree tempTree = new QLMatchResultTree(pattern.rootNodeType,nodes.get(0).createExpressNode(pattern.rootNodeType,null));
			tempTree.addLeftAll(result.matchs);
			result.matchs.clear();
			result.matchs.add(tempTree);
		}
		if(isRoot == true && log.isTraceEnabled() && result !=null){
			if(point >= nodes.size()){
				log.trace("匹配[start=" + point+":EOF]：" + pattern + (result==null?" NO ":" OK ") );
			}else{
				log.trace("匹配[start=" + point+":" + nodes.get(point) +"]：" + pattern + (result==null?" NO ":" OK ") );
			}
		}
		return result;
	}
	private  static QLMatchResult matchDetailOneTime(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		QLMatchResult result = null;
		if (pattern.matchMode == MatchMode.DETAIL) {
			if(point == nodes.size() && pattern.nodeType == aManager.findNodeType("EOF")){
				result = new QLMatchResult(new ArrayList<QLMatchResultTree>(), point);
			}else if(point == nodes.size() && pattern.nodeType.getPatternNode() != null){
				result = findMatchStatementWithAddRoot(aManager,pattern.nodeType.getPatternNode(),nodes,point,false);
			}else if( point < nodes.size()){
				INodeType tempNodeType = nodes.get(point).getTreeType();
				
				if(tempNodeType != null){
					tempNodeType = tempNodeType.isEqualsOrChildAndReturn(pattern.nodeType);
				}else{
					tempNodeType = nodes.get(point).getNodeType().isEqualsOrChildAndReturn(
							pattern.nodeType);
				}
				if(tempNodeType != null){
					List<QLMatchResultTree> tempList = new ArrayList<QLMatchResultTree>();
					tempList.add(new QLMatchResultTree(tempNodeType,nodes.get(point),pattern.targetNodeType));
					point = point + 1;
					result = new QLMatchResult(tempList, point);
				}else if(pattern.nodeType.getPatternNode() != null){
					result = findMatchStatementWithAddRoot(aManager,pattern.nodeType.getPatternNode(),nodes,point,false);
				}
			}
		}else{
			throw new Exception("不正确的类型：" + pattern.matchMode.toString());
		}
		return result;

	}	
	private  static QLMatchResult matchOrOneTime(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		QLMatchResult result = null;
		if (pattern.matchMode == MatchMode.OR) {
			for (QLPatternNode item : pattern.children) {
				QLMatchResult tempResult = findMatchStatementWithAddRoot(aManager,item,nodes,point,false);
				if (tempResult != null) {
					return tempResult;
				}
			}
		}else{
			throw new Exception("不正确的类型：" + pattern.matchMode.toString());
		}	
		return result;
	}
	private  static QLMatchResult matchAndOneTime(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		if(pattern.matchMode ==MatchMode.AND ){
			QLMatchResultTree root = null;
    		List<QLMatchResultTree> tempList = new ArrayList<QLMatchResultTree>();
			for (QLPatternNode item : pattern.children) {
				if(point > nodes.size()){
					return null;
				}
				QLMatchResult tempResult = findMatchStatementWithAddRoot(aManager,item,nodes,
						point,false);
				if (tempResult != null) {
					point = tempResult.matchLastIndex;
					if (item.isTreeRoot == true) {
						if (tempResult.matchs.size() > 1)
							throw new Exception("根节点的数量必须是1");
						if (root == null) {
							tempResult.matchs.get(0).addLeftAll(tempList);
							tempList.clear();
						} else {
							tempResult.matchs.get(0).addLeft(root);
						}
						root = tempResult.matchs.get(0);
					} else if (root != null) {
						root.addRightAll(tempResult.matchs);
					} else {
						tempList.addAll(tempResult.matchs);
					}
				}else{
					return null;
				}
			}	
			if(root != null){
				tempList.add(root);
			}
			if(tempList.size() > 0){
				return new QLMatchResult(tempList,point);
			}else{
				return null;
			}
		}else{
			throw new Exception("不正确的类型：" + pattern.matchMode.toString());
		}
	}
	
}


