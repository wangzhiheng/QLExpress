package com.ql.util.express.parse;


public class KeyWordDefine4SQL {
	public String[] splitWord={
			 "+", "-","*", "/",//四则运算：
			 ".",",",":",";","(", ")","[", "]","?",//分隔符号
			 "!","<", ">", "<=", ">=", "<>","="
	};
	public  String[] keyWords = new String[] {
			 "select","from","where","and","or"
	};
	public String[] nodeTypeDefines = new String[] {
				"in:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.InInstructionFactory",
				",:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				"EOF:TYPE=WORDDEF,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				"FUNCTION_NAME:TYPE=WORDDEF",				
				"FUNCTION_DEFINE:TYPE=WORDDEF,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"ID:TYPE=WORDDEF,FACTORY=com.ql.util.express.instruction.LoadAttrInstructionFactory",

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
				"CONST:TYPE=GROUP,CHILDREN=CONST_NUMBER|CONST_CHAR|CONST_STRING|CONST_BOOLEAN|CONST_CLASS,FACTORY=com.ql.util.express.instruction.ConstDataInstructionFactory",

				"COMMENT:TYPE=BLOCK,STARTTAG=/**,ENDTAG=**/,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				"():TYPE=BLOCK,STARTTAG=(,ENDTAG=),FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"[]:TYPE=BLOCK,STARTTAG=[,ENDTAG=],FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"{}:TYPE=BLOCK,STARTTAG={,ENDTAG=},CHILDREN=FUNCTION_DEFINE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"EXPRESS_CHILD:TYPE=GROUP,CHILDREN=()|[]",	
				
				"OP_LEVEL1:TYPE=OPERATOR,CHILDREN=!",                         
				"OP_LEVEL2:TYPE=OPERATOR,CHILDREN=*|/",                   
				"OP_LEVEL3:TYPE=OPERATOR,CHILDREN=+|-",                         
				"OP_LEVEL4:TYPE=OPERATOR,CHILDREN=in|like",                     
				"OP_LEVEL5:TYPE=OPERATOR,CHILDREN=>|>=|<|<=|=|<>",
				"OP_LEVEL6:TYPE=OPERATOR,CHILDREN=and",
				"OP_LEVEL7:TYPE=OPERATOR,CHILDREN=or",	
				
				"OP_LIST:TYPE=GROUP,CHILDREN=OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|OP_LEVEL7|(|)|[|]",

				
				
				"STAT_IFELSE:TYPE=STATEMENT,DEFINE=if^$(()|(((OPDATA|OP_LIST)*)#()))$then$({}|(((OPDATA|OP_LIST|;)*)#{}))$else$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IF:TYPE=STATEMENT,    DEFINE=if^$(()|(((OPDATA|OP_LIST)*)#()))$then$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IFELSE_JAVA:TYPE=STATEMENT,DEFINE=if^$()$({}|(((OPDATA|OP_LIST|;)*)#{}))$else$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STAT_IF_JAVA:TYPE=STATEMENT,    DEFINE=if^$()$({}|(((OPDATA|OP_LIST)*)$(;|EOF)#{})),FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				                                    
				"STAT_FOR:TYPE=STATEMENT,DEFINE=for^$()${},FACTORY=com.ql.util.express.instruction.ForInstructionFactory",
				"STAT_SEMICOLON:TYPE=STATEMENT,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"STAT_SEMICOLON_EOF:TYPE=STATEMENT,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"STAT_MACRO:TYPE=STATEMENT,DEFINE=macro^$ID->CONST_STRING${},FACTORY=com.ql.util.express.instruction.MacroInstructionFactory",
				"STAT_FUNCTION:TYPE=STATEMENT,DEFINE=function^$ID->CONST_STRING$()${},FACTORY=com.ql.util.express.instruction.FunctionInstructionFactory",
				"STAT_CLASS:TYPE=STATEMENT,DEFINE=class^$VClass->CONST_STRING$()${},FACTORY=com.ql.util.express.instruction.FunctionInstructionFactory",
				"STATEMENT:TYPE=STATEMENT,CHILDREN=STAT_SEMICOLON|STAT_SEMICOLON_EOF|STAT_FOR|STAT_IFELSE|STAT_IF|STAT_IF_JAVA|STAT_IFELSE_JAVA |STAT_MACRO ",

				
				
				"VAR_DEFINE:TYPE=EXPRESS,DEFINE=(CONST_CLASS|VClass->CONST_STRING)$[]*$ID->CONST_STRING#def,FACTORY=com.ql.util.express.instruction.DefineInstructionFactory",
				"EXPORT_VAR_DEFINE:TYPE=EXPRESS,DEFINE=exportDef^$CONST_CLASS$ID->CONST_STRING, FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"NEW_OBJECT:TYPE=EXPRESS,DEFINE=new^$CONST_CLASS$(),FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				"NEW_ARRAY:TYPE=EXPRESS,DEFINE=new^$CONST_CLASS$([]*),FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				"NEW_VIR_OBJECT:TYPE=EXPRESS,DEFINE=new^$VClass->CONST_STRING$(),FACTORY=com.ql.util.express.instruction.NewVClassInstructionFactory",
				"FIELD_CALL:TYPE=EXPRESS,DEFINE=(OPDATA$.^$ID->CONST_STRING)|(CONST_CLASS$.^$class->CONST_STRING),FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"METHOD_CALL:TYPE=EXPRESS,DEFINE=OPDATA$.^$(ID->CONST_STRING|FUNCTION_NAME->CONST_STRING)$(),FACTORY=com.ql.util.express.instruction.MethodCallInstructionFactory",
				"FUNCTION_CALL:TYPE=EXPRESS,DEFINE=(ID->FUNCTION_NAME|FUNCTION_NAME)$()#FUNCTION_CALL,FACTORY=com.ql.util.express.instruction.CallFunctionInstructionFactory",
				"CAST_CALL:TYPE=EXPRESS,DEFINE=()$OPDATA#cast,FACTORY=com.ql.util.express.instruction.CastInstructionFactory",
				"ARRAY_CALL:TYPE=EXPRESS,DEFINE=OPDATA$[]#ARRAY_CALL,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"ANONY_NEW_ARRAY:TYPE=EXPRESS,DEFINE=[]#anonymousNewArray,FACTORY=com.ql.util.express.instruction.NewInstructionFactory",
				
				"EXPRESS_LEVEL1:TYPE=EXPRESS,DEFINE=       OP_LEVEL1^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL2:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL3^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL3:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL4^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL4:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL5^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL5:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL6^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL6:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL7^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_LEVEL7:TYPE=EXPRESS,DEFINE=OPDATA$OP_LEVEL8^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_JUDGEANDSET:TYPE=EXPRESS,DEFINE=OPDATA$?$OPDATA$:$OPDATA#EXPRESS_JUDGEANDSET,FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"EXPRESS_KEY_VALUE:TYPE=EXPRESS,DEFINE=OPDATA$:^$(OPDATA|{}),FACTORY=com.ql.util.express.instruction.KeyValueInstructionFactory",
				"EXPRESS_ASSIGN:TYPE=EXPRESS,DEFINE=OPDATA$=^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				
				"EXPRESS_RETURN:TYPE=EXPRESS,DEFINE=return^$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",

				"BREAK_CALL:TYPE=EXPRESS,DEFINE=break^,FACTORY=com.ql.util.express.instruction.BreakInstructionFactory",
				"CONTINUE_CALL:TYPE=EXPRESS,DEFINE=continue^,FACTORY=com.ql.util.express.instruction.ContinueInstructionFactory",
				"ALIAS_CALL:TYPE=EXPRESS,DEFINE=alias^$ID->CONST_STRING$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPORT_ALIAS_CALL:TYPE=EXPRESS,DEFINE=exportAlias^$ID->CONST_STRING$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				
				"OP_CALL:TYPE=EXPRESS,DEFINE=(FUNCTION_NAME|OP_LIST)^$(OPDATA*),FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS:TYPE=EXPRESS,CHILDREN=EXPORT_VAR_DEFINE|VAR_DEFINE|NEW_OBJECT|NEW_ARRAY|NEW_VIR_OBJECT|ANONY_NEW_ARRAY|CAST_CALL|ARRAY_CALL|METHOD_CALL|FIELD_CALL|FUNCTION_CALL|EXPRESS_JUDGEANDSET|EXPRESS_KEY_VALUE|EXPRESS_ASSIGN|EXPRESS_LEVEL1|EXPRESS_LEVEL2|EXPRESS_LEVEL3|EXPRESS_LEVEL4|EXPRESS_LEVEL5|EXPRESS_LEVEL6|EXPRESS_LEVEL7|EXPRESS_LEVEL8|EXPRESS_LEVEL9|EXPRESS_RETURN|BREAK_CALL|CONTINUE_CALL|ALIAS_CALL|EXPORT_ALIAS_CALL|OP_CALL",

				"OPDATA:TYPE=GROUP,CHILDREN=CONST|ID|()|EXPRESS"
		};
	public String statementDefineStrs = "STAT_FUNCTION,STAT_CLASS,STAT_MACRO,STAT_FOR,STAT_IFELSE,STAT_IF,STAT_IFELSE_JAVA,STAT_IF_JAVA";
	public String[] expressDefineStrs = {
				"EXPORT_VAR_DEFINE,VAR_DEFINE,NEW_OBJECT,NEW_ARRAY,NEW_VIR_OBJECT,ARRAY_CALL,METHOD_CALL,FIELD_CALL,FUNCTION_CALL",
				"ANONY_NEW_ARRAY",
				"CAST_CALL",
				"EXPRESS_LEVEL1", "EXPRESS_LEVEL2", "EXPRESS_LEVEL3",
				"EXPRESS_LEVEL4", "EXPRESS_LEVEL5", "EXPRESS_LEVEL6",
				"EXPRESS_LEVEL7", "EXPRESS_LEVEL8","EXPRESS_LEVEL9",
				"EXPRESS_JUDGEANDSET",
				"EXPRESS_KEY_VALUE",
				"EXPRESS_ASSIGN", "BREAK_CALL,CONTINUE_CALL",
				"EXPRESS_RETURN",
				"ALIAS_CALL,EXPORT_ALIAS_CALL,OP_CALL" };
		
}
