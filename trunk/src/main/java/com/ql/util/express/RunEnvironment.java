package com.ql.util.express;



public class RunEnvironment {
		private static int INIT_DATA_LENTH = 15;
	    private boolean isTrace = false;	
		private int point = -1;
	    private int programPoint = 0;
		private OperateData[] dataContainer;
		
		private boolean isExit = false;
		private Object returnValue = null; 
		
		private InstructionSet instructionSet;
		private InstructionSetContext<String, Object> context;
		
		
		public RunEnvironment(InstructionSet aInstructionSet,InstructionSetContext<String, Object> aContext,boolean aIsTrace){
			dataContainer = new OperateData[INIT_DATA_LENTH];
			this.instructionSet = aInstructionSet;
			this.context = aContext;
			this.isTrace = aIsTrace;
		}
		
		
		public InstructionSet getInstructionSet() {
			return instructionSet;
		}


		public InstructionSetContext<String, Object> getContext(){
			return this.context;
		}
		public void setContext(InstructionSetContext<String, Object> aContext){
			this.context = aContext;
		}

		public boolean isExit() {
			return isExit;
		}
		public Object getReturnValue() {
			return returnValue;
		}
		public void setReturnValue(Object value){
			this.returnValue = value;
		}
		public void quitExpress(Object aReturnValue){
			this.isExit = true;
			this.returnValue = aReturnValue;
		}
		public boolean isTrace(){
			return this.isTrace;
		}
		public int getProgramPoint() {
			return programPoint;
		}
		public void programPointAddOne() {
			programPoint ++ ;
		}
	    public int getDataStackSize(){
	    	return this.point + 1;
	    }
		public void push(OperateData data){
			this.point++;
			if(this.point >= this.dataContainer.length){
			   ensureCapacity(this.point + 1);
			}
			this.dataContainer[point] = data;
		}
		public OperateData peek(){
			if(point <0){
				throw new RuntimeException("ϵͳ�쳣����ջָ�����");
			}
			return this.dataContainer[point];		
		}
		public OperateData pop(){
			if(point <0)
				throw new RuntimeException("ϵͳ�쳣����ջָ�����");
			OperateData result = this.dataContainer[point];
			this.point--;
			return result;
		}
		public void clearDataStack(){
			this.point = -1;
		}
		public void gotoWithOffset(int aOffset ){
			this.programPoint = this.programPoint + aOffset;
		}
	/**
	 * �˷����ǵ�����Ƶ���ģ���˾���������룬���Ч�� 
	 * @param context
	 * @param len
	 * @return
	 * @throws Exception
	 */
		public OperateData[] popArray(InstructionSetContext<String, Object> context,int len) throws Exception {
			int start = point - len + 1;
			OperateData[] result = new OperateData[len];
			System.arraycopy(this.dataContainer,start, result,0, len);
			point = point - len;
			return result;
		}
		public OperateData[] popArrayBackUp(InstructionSetContext<String, Object> context,int len) throws Exception {
			int start = point - len + 1;
			if(start <0){
				throw new Exception("��ջ�����������ʽ�Ƿ����");
			}
			OperateData[] result = new OperateData[len];
			for (int i = 0 ; i < len; i++) {
				result[i] = this.dataContainer[start + i];
				if(void.class.equals(result[i].getType(context))){
					throw new Exception("void ���ܲ����κβ�������,����ʹ���ڱ��ʽ��ʹ����û�з���ֵ�ĺ���,���߷�֧��������if���");
				}
			}
			point = point - len;
			return result;
		}

		public void ensureCapacity(int minCapacity) {
			int oldCapacity = this.dataContainer.length;
			if (minCapacity > oldCapacity) {
				int newCapacity = (oldCapacity * 3) / 2 + 1;
				if (newCapacity < minCapacity){
					newCapacity = minCapacity;
				}
				OperateData[] tempList = new OperateData[newCapacity];
				System.arraycopy(this.dataContainer,0,tempList,0,this.point + 1);
				this.dataContainer = tempList;
			}
		}
	}
