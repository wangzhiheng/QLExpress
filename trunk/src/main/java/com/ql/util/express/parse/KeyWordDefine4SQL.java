package com.ql.util.express.parse;


public class KeyWordDefine4SQL {
	public String[] splitWord={
			 "+", "-","*", "/",//四则运算：
			 ".",",",":",";","(", ")","[", "]","{","}","?",//分隔符号
			 "!","<", ">", "<=", ">=", "<>","="
	};
	public  String[] keyWords = new String[] {
			 "select","from","where","and","or","like","if","then","else"
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

				"():TYPE=BLOCK,STARTTAG=(,ENDTAG=),FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"[]:TYPE=BLOCK,STARTTAG=[,ENDTAG=],FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"{}:TYPE=BLOCK,STARTTAG={,ENDTAG=},FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"EXPRESS_CHILD:TYPE=GROUP,CHILDREN=()|[]",	
				
				"OP_LEVEL1:TYPE=OPERATOR,CHILDREN=!",                         
				"OP_LEVEL2:TYPE=OPERATOR,CHILDREN=*|/",                   
				"OP_LEVEL3:TYPE=OPERATOR,CHILDREN=+|-",                         
				"OP_LEVEL4:TYPE=OPERATOR,CHILDREN=in|like",                     
				"OP_LEVEL5:TYPE=OPERATOR,CHILDREN=>|>=|<|<=|=|<>",
				"OP_LEVEL6:TYPE=OPERATOR,CHILDREN=and",
				"OP_LEVEL7:TYPE=OPERATOR,CHILDREN=or",	
				
				"OP_LIST:TYPE=GROUP,CHILDREN=OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|OP_LEVEL7|(|)|[|]",
				"COL_NAME:TYPE=STATEMENT,DEFINE=ID$(.$ID)*#COL_NAME,FACTORY=com.ql.util.express.instruction.LoadAttrInstructionFactory",
				"PARAMETER_LIST:TYPE=STATEMENT,DEFINE=(COL_NAME|CONST)$(,~$(COL_NAME|CONST))*#PARAMETER_LIST,FACTORY=com.ql.util.express.instruction.LoadAttrInstructionFactory",
				
				
				"STATEMENT:TYPE=STATEMENT",
				"EXPRESS:TYPE=STATEMENT",
				"OPDATA:TYPE=GROUP,CHILDREN=CONST|ID|()|EXPRESS"
		};
	public String statementDefineStrs = "";
	public String[] expressDefineStrs = {
				};
		
}
