package com.ql.util.express.test.newmatch;

import java.util.List;

import org.junit.Test;

import com.ql.util.express.match.QLMatchResult;
import com.ql.util.express.match.QLPattern;
import com.ql.util.express.match.QLPatternNode;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.KeyWordDefine4Java;
import com.ql.util.express.parse.NodeTypeManager;
import com.ql.util.express.parse.Word;
import com.ql.util.express.parse.WordSplit;

public class PatternTest {

	
	@Test
	public void testMatch() throws Exception{
		String[][] defines = new String[][]{
			//	{"EXPRESS","(3-2)*(2-1)"},
				//	{"OPDATA","ABC.B"},
			//	{"EXPRESS_OP_L1","!ABC"},
			//	{"EXPRESS","!!3 * !!4 * 5 + 8 + 7 +9 like ABC"},
			//	{"EXPRESS_OP_L5","7+!!3*4 like 9 +!!2*4"},
			//	{"SELECT","select TAB.TABLE_NAME  + '-ABC',TAB.COL_NAME.B  * 100 from A,B"},
				{"EXPRESS","(new int[3][5])[1].length"},
				
		};
		NodeTypeManager manager = new NodeTypeManager(new KeyWordDefine4Java());
		ExpressParse parse = new ExpressParse(manager,null,false);
		for(String[] row : defines){
			Word[] words = WordSplit.parse(manager.splitWord,row[1]);
//			System.out.println("���ʷֽ���:" + WordSplit.getPrintInfo(words,","));  
			List<ExpressNode> tempList = parse.transferWord2ExpressNode(null,words,null,true);
			System.out.println("���ʷ������:" + ExpressParse.printInfo(tempList,","));
			QLPatternNode pattern = manager.findNodeType(row[0]).getPatternNode();
			QLMatchResult result =QLPattern.findMatchStatement(manager, pattern, tempList, 0);
			if(result == null){
				throw new Exception("û����ȷ��ƥ�䣺" + row[0] + ":" + row[1]);
			}
			System.out.println(result);
		}
		
	} 
}
