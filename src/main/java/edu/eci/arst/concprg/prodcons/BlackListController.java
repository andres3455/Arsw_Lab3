package edu.eci.arst.concprg.prodcons;

public class BlackListController {
    

    private static final int AlarmCountBlackList = 5;
    private int ocurrencesCount = 0;

    public void InnerBlackListController (){
        
    }

    public synchronized boolean IncrementOcurrence(){
        boolean isvalid = false;
        if (ocurrencesCount<AlarmCountBlackList){
            ocurrencesCount++;
            isvalid = true;
        }

        return isvalid;
    }

    public synchronized boolean validate(){
        boolean isvalid = false;
        if(ocurrencesCount==AlarmCountBlackList){
            isvalid = true;
        }
        return isvalid;
    }
}
