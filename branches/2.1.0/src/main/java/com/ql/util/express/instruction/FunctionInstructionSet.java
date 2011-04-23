package com.ql.util.express.instruction;

import com.ql.util.express.InstructionSet;


public class FunctionInstructionSet{
	public String name;
	public String type;
	public InstructionSet instructionSet;
	public FunctionInstructionSet(String aName,String aType,InstructionSet aInstructionSet){
		this.name = aName;
		this.type = aType;
		this.instructionSet = aInstructionSet;		
	}
	
}