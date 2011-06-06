package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeTypeManager {
	private String[] keyWords = new String[] {
			 "~","&","|","<<", ">>",//位操作 
			 "+", "-","*", "/", "%","mod","++", "--",//四则运算：
			 ".",";","(", ")", "{", "}", "[", "]","?",//分隔符号
			 "!","<", ">", "<=", ">=", "==","!=","&&","||",//Boolean运算符号
			 "for", "if", "then", "else", "exportAlias", "alias",
			 "break", "continue", "return", "macro", "function" ,
			 "def","exportDef", "new","array","anonymousNewArray",
			 "like",
			 "=","cast","/**","**/"
	};
		private String[] nodeTypeDefines = new String[] {
				"EOF:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				"FUNCTION_NAME:TYPE=KEYWORD",
				"in:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.InInstructionFactory",
				"CONST_BYTE:TYPE=CONST",
				"CONST_SHORT:TYPE=CONST",
				"CONST_INTEGER:TYPE=CONST",
				"CONST_LONG:TYPE=CONST",
				"CONST_FLOAT:TYPE=CONST",
				"CONST_DOUBLE:TYPE=CONST",

				"CONST_NUMBER:TYPE=CONST,CHILDREN=CONST_BYTE|CONST_SHORT|CONST_INTEGER|CONST_LONG|CONST_FLOAT|CONST_DOUBLE",
				"CONST_CHAR:TYPE=CONST",
				"CONST_STRING:TYPE=CONST",
				"CONST_BOOLEAN:TYPE=CONST",
				"CONST_CLASS:TYPE=CONST,FACTORY=com.ql.util.express.instruction.ConstDataInstructionFactory",
				"CONST:TYPE=CONST,CHILDREN=CONST_NUMBER|CONST_CHAR|CONST_STRING|CONST_BOOLEAN|CONST_CLASS,FACTORY=com.ql.util.express.instruction.ConstDataInstructionFactory",

				",:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				"::TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"COMMENT:TYPE=BLOCK,STARTTAG=/**,ENDTAG=**/,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				
				"():TYPE=BLOCK,STARTTAG=(,ENDTAG=),FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"[]:TYPE=BLOCK,STARTTAG=[,ENDTAG=],FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"{}:TYPE=BLOCK,STARTTAG={,ENDTAG=},CHILDREN=FUNCTION_DEFINE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",

				"STAT_IFELSE:TYPE=TREETYPE,DEFINE=(if^)$(()|(((OPDATA|OP_LIST)*)#()))$then$({}|(((OPDATA|OP_LIST|;)*)#{}))$else$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IF:TYPE=TREETYPE,    DEFINE=(if^)$(()|(((OPDATA|OP_LIST)*)#()))$then$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IFELSE_JAVA:TYPE=TREETYPE,DEFINE=(if^)$()$({}|(((OPDATA|OP_LIST|;)*)#{}))$else$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IF_JAVA:TYPE=TREETYPE,    DEFINE=(if^)$()$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				                                    
				"STAT_FOR:TYPE=TREETYPE,DEFINE=(for^)$()${},FACTORY=com.ql.util.express.instruction.ForInstructionFactory",
				"STAT_SEMICOLON:TYPE=TREETYPE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"STAT_SEMICOLON_EOF:TYPE=TREETYPE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"STAT_MACRO:TYPE=TREETYPE,DEFINE=(macro^)$ID->CONST_STRING${},FACTORY=com.ql.util.express.instruction.MacroInstructionFactory",
				"STAT_FUNCTION:TYPE=TREETYPE,DEFINE=(function^)$ID->CONST_STRING$()${},FACTORY=com.ql.util.express.instruction.FunctionInstructionFactory",
				"STATEMENT:TYPE=TREETYPE,CHILDREN=STAT_SEMICOLON|STAT_SEMICOLON_EOF|STAT_FOR|STAT_IFELSE|STAT_IF|STAT_IF_JAVA|STAT_IFELSE_JAVA |STAT_MACRO ",

				"FUNCTION_DEFINE:TYPE=DEFINE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"ID:TYPE=SYMBOL,FACTORY=com.ql.util.express.instruction.LoadAttrInstructionFactory",
				
				"EXPRESS_CHILD:TYPE=TREETYPE,CHILDREN=()|[]",	
				
				"OP_LEVEL1:TYPE=OPERATOR,CHILDREN=~|!",                         
				"OP_LEVEL2:TYPE=OPERATOR,CHILDREN=++|--",                       
				"OP_LEVEL3:TYPE=OPERATOR,CHILDREN=&|\\||<<|>>",                 
				"OP_LEVEL4:TYPE=OPERATOR,CHILDREN=*|/|mod|%",                   
				"OP_LEVEL5:TYPE=OPERATOR,CHILDREN=+|-",                         
				"OP_LEVEL6:TYPE=OPERATOR,CHILDREN=in|like",                     
				"OP_LEVEL7:TYPE=OPERATOR,CHILDREN=>|>=|<|<=|==|!=",
				"OP_LEVEL8:TYPE=OPERATOR,CHILDREN=&&",
				"OP_LEVEL9:TYPE=OPERATOR,CHILDREN=\\|\\|",	
				
				"OTHER_KEYWORD:TYPE=TREETYPE,CHILDREN=exportAlias|alias|break|continue|return|def|exportDef|new",
				"OP_LIST:TYPE=TREETYPE,CHILDREN=OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|OP_LEVEL7||OP_LEVEL8|OP_LEVEL9|=|OTHER_KEYWORD|(|)|[|]|{|}",
				
				"VAR_DEFINE:TYPE=TREETYPE,DEFINE=CONST_CLASS$ID->CONST_STRING#def,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPORT_VAR_DEFINE:TYPE=TREETYPE,DEFINE=(exportDef^)$CONST_CLASS$ID->CONST_STRING, FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"NEW_OBJECT:TYPE=TREETYPE,DEFINE=(new^)$CONST_CLASS$(),FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				"NEW_ARRAY:TYPE=TREETYPE,DEFINE=(new^)$CONST_CLASS$([]*),FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				"FIELD_CALL:TYPE=TREETYPE,DEFINE=OPDATA$(.^)$ID->CONST_STRING,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"METHOD_CALL:TYPE=TREETYPE,DEFINE=OPDATA$(.^)$(ID->CONST_STRING|FUNCTION_NAME->CONST_STRING)$(),FACTORY=com.ql.util.express.instruction.MethodCallInstructionFactory",
				"FUNCTION_CALL:TYPE=TREETYPE,DEFINE=(ID->FUNCTION_NAME|FUNCTION_NAME)$()#FUNCTION_CALL,FACTORY=com.ql.util.express.instruction.CallFunctionInstructionFactory",
				"CAST_CALL:TYPE=TREETYPE,DEFINE=()$OPDATA#cast,FACTORY=com.ql.util.express.instruction.CastInstructionFactory",
				"ARRAY_CALL:TYPE=TREETYPE,DEFINE=OPDATA$[]#ARRAY_CALL,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"ANONY_NEW_ARRAY:TYPE=TREETYPE,DEFINE=[]#anonymousNewArray,FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				
				"EXPRESS_LEVEL1:TYPE=TREETYPE,DEFINE=       (OP_LEVEL1^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL2:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL2^),       FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL3:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL3^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL4:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL4^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL5:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL5^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL6:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL6^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL7:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL7^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_LEVEL8:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL8^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_LEVEL9:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL9^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_JUDGEANDSET:TYPE=TREETYPE,DEFINE=OPDATA$?$OPDATA$:$OPDATA#EXPRESS_JUDGEANDSET,FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"EXPRESS_KEY_VALUE:TYPE=TREETYPE,DEFINE=OPDATA$(:^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_ASSIGN:TYPE=TREETYPE,DEFINE=OPDATA$(=^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				
				"EXPRESS_RETURN_DATA:TYPE=TREETYPE,DEFINE=(return^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_RETURN_NULL:TYPE=TREETYPE,DEFINE=return^,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				
				"EXPRESS_RETURN:TYPE=TREETYPE,CHILDREN=EXPRESS_RETURN_DATA|EXPRESS_RETURN_NULL",
				
				"BREAK_CALL:TYPE=TREETYPE,DEFINE=break^,FACTORY=com.ql.util.express.instruction.BreakInstructionFactory",
				"CONTINUE_CALL:TYPE=TREETYPE,DEFINE=continue^,FACTORY=com.ql.util.express.instruction.ContinueInstructionFactory",
				"ALIAS_CALL:TYPE=TREETYPE,DEFINE=(alias^)$ID->CONST_STRING$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPORT_ALIAS_CALL:TYPE=TREETYPE,DEFINE=(exportAlias^)$ID->CONST_STRING$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"OP_CALL:TYPE=TREETYPE,DEFINE=((FUNCTION_NAME|OP_LIST)^)$(OPDATA*),FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				
				"EXPRESS:TYPE=TREETYPE,CHILDREN=EXPORT_VAR_DEFINE|VAR_DEFINE|NEW_OBJECT|NEW_ARRAY|ANONY_NEW_ARRAY|CAST_CALL|ARRAY_CALL|METHOD_CALL|FIELD_CALL|FUNCTION_CALL|EXPRESS_JUDGEANDSET|EXPRESS_KEY_VALUE|EXPRESS_ASSIGN|EXPRESS_LEVEL1|EXPRESS_LEVEL2|EXPRESS_LEVEL3|EXPRESS_LEVEL4|EXPRESS_LEVEL5|EXPRESS_LEVEL6|EXPRESS_LEVEL7|EXPRESS_LEVEL8|EXPRESS_LEVEL9|EXPRESS_RETURN_DATA|EXPRESS_RETURN_NULL|BREAK_CALL|CONTINUE_CALL|ALIAS_CALL|EXPORT_ALIAS_CALL|OP_CALL",
				"OPDATA:TYPE=TREETYPE,CHILDREN=CONST|ID|()|EXPRESS"
		};
		protected String statementDefineStrs = "STAT_FUNCTION,STAT_MACRO,STAT_FOR,STAT_IFELSE,STAT_IF,STAT_IFELSE_JAVA,STAT_IF_JAVA";
		protected String[] expressDefineStrs = {
				"EXPORT_VAR_DEFINE,VAR_DEFINE,NEW_OBJECT,NEW_ARRAY,ARRAY_CALL,METHOD_CALL,FIELD_CALL,FUNCTION_CALL",
				"ANONY_NEW_ARRAY",
				"CAST_CALL",
				"EXPRESS_LEVEL1", "EXPRESS_LEVEL2", "EXPRESS_LEVEL3",
				"EXPRESS_LEVEL4", "EXPRESS_LEVEL5", "EXPRESS_LEVEL6",
				"EXPRESS_LEVEL7", "EXPRESS_LEVEL8","EXPRESS_LEVEL9",
				"EXPRESS_JUDGEANDSET",
				"EXPRESS_KEY_VALUE",
				"EXPRESS_ASSIGN", "BREAK_CALL,CONTINUE_CALL",
				"EXPRESS_RETURN_DATA", "EXPRESS_RETURN_NULL",
				"ALIAS_CALL,EXPORT_ALIAS_CALL,OP_CALL" };
		
		protected List<NodeType> statementDefine = new ArrayList<NodeType>();
		protected List<List<NodeType>> expressDefine = new ArrayList<List<NodeType>>();
	    public NodeType S_STATEMNET = null;
	    public NodeType S_ID = null;
	    
	    protected Map<String,NodeType> nodeTypes = new HashMap<String,NodeType>();	
	    
	    //所有的函数定义
	    protected Map<String,String> functions = new HashMap<String,String>();
	    
		public NodeTypeManager() {
			//创建所有的关键字
			NodeType[] tempKeyWordNodeTypes = new NodeType[keyWords.length];
			for (int i = 0; i < tempKeyWordNodeTypes.length; i++) {
				tempKeyWordNodeTypes[i] = this.createNodeType(keyWords[i] + ":TYPE=KEYWORD");
			}
			// 初始化所有的类型信息，
			for (int i = 0; i < tempKeyWordNodeTypes.length; i++) {
				tempKeyWordNodeTypes[i].initial();
			}
			
			// 创建所有的类型信息，但不能初始化
			NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
			for (int i = 0; i < nodeTypeDefines.length; i++) {
				nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
			}
			// 初始化所有的类型信息，
			for (int i = 0; i < nodeTypes.length; i++) {
				nodeTypes[i].initial();
			}
			String[] tempStrList = statementDefineStrs.split("\\,");
			for (String item : tempStrList) {
				statementDefine.add(this.findNodeType(item));
			}

			for (String item : expressDefineStrs) {
				String[] tempList = item.split("\\,");
				List<NodeType> tempNodeTypeList = new ArrayList<NodeType>();
				for (String str:tempList) {
					tempNodeTypeList.add(this.findNodeType(str));
				}
				expressDefine.add(tempNodeTypeList);
			}
			S_STATEMNET = this.findNodeType("STATEMENT");
			S_ID = this.findNodeType("ID");
			
			this.addOperatorWithRealNodeType("and","&&");
			this.addOperatorWithRealNodeType("or","||");
			
//			for(NodeType item:this.getNodeTypesSortByKind()){
//				System.out.println(item);
//			}
		}
	    
	/**
	 * 创建节点类型，需要注意的是不能初始化，必须所有的类型都创建完成后才能调用初始化方法
	 * @param aDefineStr
	 * @return
	 */
	public NodeType createNodeType(String aDefineStr){		
		int index = aDefineStr.indexOf(":",1);//避免对操作符号":"的错误处理
		String name = aDefineStr.substring(0,index).trim();
		NodeType define = nodeTypes.get(name);
		if(define != null ){
			throw new RuntimeException("节点类型定义重复:"+name+" 定义1="+define.getDefineStr() + " 定义2=" + aDefineStr);
		}
		define = new NodeType(this,name,aDefineStr);
		nodeTypes.put(name, define);
		return define;
	}
	/**
	 * 根据类型名称查找节点类型
	 * @param name
	 * @return
	 */
	public NodeType findNodeType(String name){		
		NodeType result = nodeTypes.get(name);
		if(result == null){
			throw new RuntimeException("没有定义的节点类型：" + name);
		}
		while(result.getRealNodeType() != null){
			result = result.getRealNodeType();
		}
		return result;
	}
	
	/**
	 * 增加关键字，但是用实际的类型代替，例如 :"如果"->"if"
	 * @param keyWordName
	 * @param realName
	 */
	public void addOperatorWithRealNodeType(String keyWordName, String realName){
		NodeType target =  this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
		target.initial();
	}
	
	/**
	 * 增加新的操作符号，其优先级别，以及语法关系与参照的操作符号一致
	 * @param operName
	 * @param refOperName
	 */
	public void addOperatorWithLevelOfReference(String operName, String refOperName){
		NodeType target =  this.createNodeType(operName + ":TYPE=KEYWORD");
		target.initial();
		NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
		NodeType refNodeType = this.findNodeType(refOperName);
		target.setInstructionFactory(refNodeType.getInstructionFactory());
		for(NodeType item:list){
			if(item.isContainsChild(refNodeType)){
				item.addChild(target);
				return;
			}
		}		
	}
	
	/**
	 * 判断是否存在节点类型定义
	 * @param name
	 * @return
	 */
	public NodeType isExistNodeTypeDefine(String name){
		NodeType result = nodeTypes.get(name);
		if(result != null && result.getRealNodeType() != null){				
		  result = result.getRealNodeType();
		}
		return result;
	}
	/**
	 * 递归判断节点类型是否匹配，例如 :
	 * isNodeTypeOrChild(CONST_LONG,CONST)==true
	 * isNodeTypeOrChild(CONST_LONG,CONST_NUMBER)==true
	 * isNodeTypeOrChild(CONST_LONG,CONST_BOOLEAN)==false
	 * @param child
	 * @param parent
	 * @return
	 */
	public NodeType isEqualsOrChildAndReturn2(NodeType child,NodeType parent){
		if (child == parent)
			return child;
		NodeType[] tempList = parent.getChildren();
		if (tempList != null) {
			for (NodeType item : tempList) {
				NodeType result = isEqualsOrChildAndReturn2(child, item);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
	
	public NodeType[] getNodeTypesByKind(NodeTypeKind aKind){
		List<NodeType> result  = new ArrayList<NodeType>();
		for(NodeType item :this.nodeTypes.values()){
			if(item.getKind() == aKind){
				result.add(item);
			}
		}
		return result.toArray(new NodeType[0]);
	}
	public NodeType[] getNodeTypesSortByKind(){
		NodeType[] list = nodeTypes.values().toArray(new NodeType[0]);
		Arrays.sort(list,new NodeTypeComparator());
		return list;
	}
	public boolean isFunction(String name){
		return this.functions.containsKey(name);
	}
	public void addFunctionName(String name){
		this.functions.put(name, name);
	}
	public static String[] splist(String str,char splitChar,boolean isIncludeSplitChar){
		List<String> result = new ArrayList<String>();
		String tempStr ="";
		for(int i=0;i<str.length();i++){
			if (str.charAt(i) == '\\') {
				tempStr = tempStr + str.charAt(i + 1);
				i = i + 1;
			} else if (str.charAt(i) == splitChar) {
				if(tempStr.length() >0){
				    result.add(tempStr);
				}
				if(isIncludeSplitChar == true){
					result.add(splitChar + "");
				}
				tempStr = "";
			} else {
				tempStr = tempStr + str.charAt(i);
			}
		}
		if(tempStr.length() >0){
			result.add(tempStr);
		}
		return result.toArray(new String[0]);
	}
}
