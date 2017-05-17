package tagabukid.subay.models;

import com.rameses.rcp.annotations.* 
import com.rameses.rcp.common.* 
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import com.rameses.util.*;
import com.rameses.common.*;
import com.rameses.gov.etracs.rpt.util.*;


public class OnlineTransactionTask implements Runnable{
    def entity;
//    def reader;
    def updateItem;
    def oncomplete;
    def onerror;
    def txnSvc; 
    def txnModel;
//    def transmittal;
    
    public void run(){
        try{
//            reader = new ObjectReader(entity);
//            def data = reader.readObject();
//            transmittal = data.transmittal;
//            while(entity){
                
            txnSvc.processDocument(entity);
//                if (data.filetype == 'transmittal'){
//                    importSvc.importTransmittal(data.transmittal);
//                }
//                else{
//                    def item = importModel.importData(transmittal, data);
//                    updateItem(item);
//                }
//                data = reader.readObject();
//            }
            oncomplete('Transmittal is successfully imported.');
        }
        catch(e){
            onerror(e.message);
        }
//        finally{
//            try { 
//                reader.close();
//            } 
//            catch(e){
//                //ignore
//            }
//        }
    }
       
}