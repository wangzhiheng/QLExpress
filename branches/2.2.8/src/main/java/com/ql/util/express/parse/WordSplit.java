
package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * �﷨������
 * 1�����ʷֽ�
 * @author xuannan
 *
 */

public class WordSplit
{

	  private static final Log log = LogFactory.getLog(WordSplit.class);
	  
	   public static  void splitOperator(NodeTypeManager nodeManager,String opStr,List<Word> objList,int aLine,int aCol){
		     String orgiStr = opStr;
		     while (opStr.length() >0){ //����������ִ�
		          boolean isFind = false;
		          int index =opStr.length();
		          while (index > 0)
		          {
		              if (nodeManager.isExistNodeTypeDefine(opStr.substring(0,index)) != null)
		              {
		            	 objList.add(new Word(opStr.substring(0,index),aLine,aCol));
		                 opStr = opStr.substring(index);
		                 isFind = true;
		                 break;
		              }
		              else
		                index = index - 1;
		          }
		          if(isFind == false){
		        	 log.error("����ʶ��ķ��Ŷ��壺\"" + opStr.substring(0,1) +"\":" + "\"" + orgiStr +"\"" ); 
		             opStr = opStr.substring(1);
		          }   
		     }
		  }
   protected static boolean isNumber(String str){
    if(str == null || str.equals(""))
      return false;
    char c = str.charAt(0);
    if (c >= '0' && c <= '9') { //����
      return true;
    }
    else {
      return false;
    }
  }
  /**
   * �ı�������������.����Ϊ�������Ŵ���
   * @param str String
   * @throws Exception
   * @return String[]
   */
   public static Word[] parse(NodeTypeManager nodeManager,String str) throws Exception
  {
    if (str == null){
       return new Word[0];
    }
    String tmpWord ="";
    String tmpOpStr ="";
    char c;
    int line =1;
    List<Word> list = new ArrayList<Word>();
    int i= 0;
    while(i<str.length())
    {
       c = str.charAt(i);
      if (c=='"' || c=='\''){//�ַ�������        
    	int index = str.indexOf(c,i + 1);
    	//�����ַ����еġ�����
        while(index >0 && str.charAt(index - 1) =='\\'){
        	index = str.indexOf(c,index + 1);
        }
        if (index < 0)
        	throw new Exception("�ַ���û�йر�");
        //�Ƚ�������������У�����������
        splitOperator(nodeManager,tmpOpStr,list,line,i);
        tmpOpStr="";
        if (tmpWord.length() >0){
            list.add(new Word(tmpWord,line,i));
            tmpWord  = "";
        }
        String tempDealStr = str.substring(i,index + 1);
        //���� \\��\"�����
        String tmpResult = "";
        int tmpPoint = tempDealStr.indexOf("\\");        
        while(tmpPoint >=0 ){
        	tmpResult = tmpResult + tempDealStr.substring(0,tmpPoint);
        	if(tmpPoint == tempDealStr.length() -1){
        		throw new Exception("�ַ����е�" + "\\����:" + tempDealStr);
        	}
        	tmpResult = tmpResult + tempDealStr.substring(tmpPoint + 1 ,tmpPoint + 2);
        	tempDealStr = tempDealStr.substring(tmpPoint + 2);
        	tmpPoint = tempDealStr.indexOf("\\");  
        }
        tmpResult = tmpResult + tempDealStr;
        list.add(new Word(tmpResult,line,i));
        i = index + 1;
      }else if (((c >='0') && (c <='9'))
            || ((c >='a') && (c <='z'))
            || ((c >='A') && (c <='Z'))
            || (c=='\'')
            || (c=='$')
            || (c=='@')            
            || (c=='_')
            || (c > 127))  //��׼�ַ�
       {
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(nodeManager,tmpOpStr,list,line,i);
           tmpOpStr = "";
       }else if(c=='.' && isNumber(tmpWord) == true){
           //�����֣������ݴ���
           tmpWord = tmpWord + c;
           i = i + 1;
           splitOperator(nodeManager,tmpOpStr,list,line,i);
           tmpOpStr = "";
       }else{	   
         if (tmpWord.length() >0)
         {    list.add(new Word(tmpWord,line,i));
              tmpWord = "";
         }
         if(c == ' ' ||c =='\r'|| c =='\n'||c=='\t'||c=='\u000C'){
        	 if(c =='\n'){
        		 line = line + 1;
        	 }
        	 splitOperator(nodeManager,tmpOpStr,list,line,i);
        	 tmpOpStr = "";
         }else{
             tmpOpStr = tmpOpStr + c;
         }
         i = i + 1;
       }
    }

    if (tmpWord.length() >0)
    {    list.add(new Word(tmpWord,line,i));
         tmpWord = "";
    }
    splitOperator(nodeManager,tmpOpStr,list,line,i);

    Word result[] = new Word[list.size()];
    list.toArray(result);
    return result;
  }

   public static String getPrintInfo(Object[] list,String splitOp){
	  	StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.length;i++){
			if(i > 0){buffer.append(splitOp);}
			buffer.append("{" + list[i] +"}");
		}
		return buffer.toString();
	  }

}
