package com.ql.util.express.parse;



public class KeyWordDefine4SQL {
	public  String[] keyWords = new String[] {
			 "+", "-","*", "/", "%","mod",
			 ".","(", ")","[","]","{","}","?",//·Ö¸ô·ûºÅ
			 "<", ">", "<=", ">=", "=","<>","and","or",//BooleanÔËËã·ûºÅ
			 "like","/**","**/",
			 "select","from","where","group","by","order",";","SQL"
	};
	public String[] nodeTypeDefines = new String[] {
				"EOF:TYPE=KEYWORD,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
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

				"COMMENT:TYPE=BLOCK,STARTTAG=/**,ENDTAG=**/,FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				
				"():TYPE=BLOCK,STARTTAG=(,ENDTAG=),FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"[]:TYPE=BLOCK,STARTTAG=[,ENDTAG=],FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"{}:TYPE=BLOCK,STARTTAG={,ENDTAG=},CHILDREN=FUNCTION_DEFINE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				
				"FUNCTION_DEFINE:TYPE=DEFINE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"ID:TYPE=SYMBOL,FACTORY=com.ql.util.express.instruction.LoadAttrInstructionFactory",
				"EXPRESS_CHILD:TYPE=TREETYPE,CHILDREN=()|[]",	
				
				"OP_LEVEL1:TYPE=OPERATOR,CHILDREN=*|/|mod|%",                   
				"OP_LEVEL2:TYPE=OPERATOR,CHILDREN=+|-",                         
				"OP_LEVEL3:TYPE=OPERATOR,CHILDREN=in|like",                     
				"OP_LEVEL4:TYPE=OPERATOR,CHILDREN=>|>=|<|<=|=|<>",
				"OP_LEVEL5:TYPE=OPERATOR,CHILDREN=and|or",
				"OP_LEVEL6:TYPE=OPERATOR,CHILDREN=,",
				
				"OP_LIST:TYPE=TREETYPE,CHILDREN=OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|(|)|[|]",
				
				
				"EXPRESS_LEVEL1:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL1^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL2:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL2^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL3:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL3^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL4:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL4^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",
				"EXPRESS_LEVEL5:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL5^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			
				"EXPRESS_LEVEL6:TYPE=TREETYPE,DEFINE=OPDATA$(OP_LEVEL6^)$OPDATA,FACTORY=com.ql.util.express.instruction.OperatorInstructionFactory",			

				"EXPRESS:TYPE=TREETYPE,CHILDREN=EXPRESS_LEVEL1|EXPRESS_LEVEL2|EXPRESS_LEVEL3|EXPRESS_LEVEL4|EXPRESS_LEVEL5|EXPRESS_LEVEL6",
				"OPDATA:TYPE=TREETYPE,CHILDREN=CONST|ID|()|EXPRESS",
				
				"STAT_SEMICOLON:TYPE=TREETYPE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				"STAT_SEMICOLON_EOF:TYPE=TREETYPE,FACTORY=com.ql.util.express.instruction.BlockInstructionFactory",
				
				"COL_LIST:TYPE=TREETYPE,DEFINE=((ID|CONST)$((,$(ID|CONST))*)),FACTORY=com.ql.util.express.instruction.NullInstructionFactory",
				
				"STAT_SELECT:TYPE=TREETYPE,DEFINE=(select^$COL_LIST)$(from^$COL_LIST)$(where^$ID)#SQL,FACTORY=com.ql.util.express.instruction.IfInstructionFactory",
				"STATEMENT:TYPE=TREETYPE,CHILDREN=STAT_SEMICOLON|STAT_SEMICOLON_EOF|STAT_SELECT",
		};
	public String statementDefineStrs = "STAT_SELECT";
	public String[] expressDefineStrs = {
				"EXPRESS_LEVEL1", "EXPRESS_LEVEL2", "EXPRESS_LEVEL3",
				"EXPRESS_LEVEL4", "EXPRESS_LEVEL5","EXPRESS_LEVEL6"
				};

}
