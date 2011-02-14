package com.ql.util.express.instruction;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.parse.ExpressNode;

public class ForRelBreakContinue{
	 ExpressNode node;
	 List<InstructionGoTo> breakList = new ArrayList<InstructionGoTo>();
	 List<InstructionGoTo> continueList = new ArrayList<InstructionGoTo>();
	 public ForRelBreakContinue(ExpressNode aNode){
		 node = aNode;
	 }
}