import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*;
import com.rameses.util.*;
        
public class DTSProcessOnlineController {
    @Binding
    def binding;
    
    @Service("TagabukidDTSService")
    def svc
    
    def din;
    def entity;
    def document;
    def mode = "INIT"
    def selectedItem;
    
    @FormTitle
    def title
    
    void init(){
        mode= "INIT"
        title="Select Transaction Mode"
        document =[];
        din = "";
        entity = [:]
        listHandler.reload();
    }
    
    def listHandler = [
        fetchList : { return document },
        onRemoveItem : {
            if (MsgBox.confirm('Delete item?')){                
                document.remove(it)
                listHandler.reload();
                return true;
            }
            return false;
        },
        onColumnUpdate:{item,colName ->
            document.each{y ->
                if (item.din == y.din){
                    y.message = item.remarks
                }
            }
        }
    ] as EditorListModel
    
    def addtolist(def doc){
        checkDuplicate(document,doc);
        document << doc;
        din = ""
        binding.refresh('din');
        binding.focus('din');
        listHandler.reload();
    }
    
    def searchdocument(){
        def doc = svc.lookupDIN(entity)
        if (!doc){
            throw new Exception("Invalid DIN");
        }
        
        if (doc.size == 1){
            addtolist(doc[0])
        }
        else if(doc.size > 1){
            return Inv.lookupOpener('din:lookup',[
                    entity: entity,
                    onselect :{
                        addtolist(it)
                    }
                ])
        }else{
            throw new Exception("Invalid DIN");
        }
        
        
    }

    public def barcodesearch(){
        //        if (!din){
        //            throw new Exception("Invalid DIN");
        //        }
        entity.searchtext = din
        entity.mode = mode
        entity.document = document
        searchdocument()
    }
    

    void receive (){
        mode = "receive"
        title= "RECEIVING"
        binding.refresh('din');
        binding.focus('din');
    }
    void outgoing (){
        mode = "outgoing"
        title= "OUTGOING"
        entity.sendtype = 'localoutgoing'
        binding.refresh('din');
        binding.focus('din');
    }
    void send (){
        mode = "send"
        title= "SENDING"
        entity.sendtype = 'local'
        binding.refresh('din');
        binding.focus('din');
    }
    void archive (){
        mode = "archive"
        title= "ARCHIVING"
        binding.refresh('din');
        binding.focus('din');
    }
   
    def getHandler(){
        if(mode!="INIT"){
            if(mode=="send" || mode=="outgoing"){
                return InvokerUtil.lookupOpener(entity.sendtype+':form',[
                        entity: entity
                    ]) 
            }else{
                return InvokerUtil.lookupOpener(mode+':form',[
                        entity: entity
                    ])  
            }
            
        }
    }
    
    void save(){
        
        if (!entity.org && !entity.destinations && mode.matches('send|archive')) 
        throw new Exception("Destination is Required");
        
        if (!entity.assignee && !entity.oic && mode=='send') 
        throw new Exception("OIC is Required");
        
        if (document.size == 0)
        throw new Exception("Please add at least 1 document to process");
        
        entity.document = document
        entity.mode = mode
        try{
            svc.processDocument(entity)
            MsgBox.alert("Transcation Successfull")
            init()
        } catch(Warning w) {
            Modal.show( 'document_redflag:warning', [list: w.info.list] );
            throw new BreakException();
        } catch(e) {
            throw e;
        }
       
        
    }
    
    void checkDuplicate(listtofilter,item){
        def data = listtofilter.find{it.din == item.din }
        if (data)
        throw new Exception("Duplicate item is not allowed.")
    }
    
    
    //    def processDocument(){
    //        if(mode.matches('send|archive')){
    //          
    //        }
    //        
    //        println entity
    //        //createtask(mode)
    //      
    //        //if outgoing
    //        //end prev task
    //        //create new outgoing task
    //        //org = current user organization
    //        //assignee = current user
    //        //actor = current user
    //        
    //        //if sending
    //        //ask destination/org
    //        //ask assignee
    //        //actor = current user
    //        //end prev task
    //        //create new enroute task
    //        
    //        //if receiving
    //        //assignee = user
    //        //actor = user
    //        //org = current user org
    //        //end prev task
    //        //create new processing task
    //        
    //        //if archive
    //        //ask archive destination
    //        //ask cabinet/folder no/catalog
    //        //assignee = current user
    //        //actor = current user
    //        //org = current user org
    //        //end prev task
    //        //create new archive task
    //      
    ////        return Inv.lookupOpener('din:lookup',[
    ////                entity: entity,
    ////                onselect :{
    ////                    addtolist(it)
    ////                }
    ////            ])
    //        //svc.processDocument(entity)
    //    }
}