import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*;
import com.rameses.osiris2.reports.*;
        
class  DocumentRedFlagSectionController  {
    @Binding
    def binding;
    
    @Caller
    def caller
    
    def entity;
    def title = "Red Flags"
            
    @Service("TagabukidSubayRedflagService")
    def service;
    
    @Service("TagabukidSubayDocumentService")
    def svc
            
    def selectedItem;
    
    def refresh(){
        def newlog = svc.lookupNode([refid:entity.objid,taskid:entity.taskid])
           
        if (newlog.size == 1){
            entity = newlog[0]
        }else if (newlog.size > 1){
            return Inv.lookupOpener('node:lookup',[
                    entity: entity,
                    onselect :{
                        entity = it
                        redflagListModel.reload()
                    }
                ])
        }
        
       
        redflagListModel.reload();
        binding.refresh();  
        caller.entity = entity;
        caller.reloadSections();
    }
    def redflagListModel = [
        fetchList: { o->
            return service.getList( entity );
        }
    ] as BasicListModel;
            
    def addRedflag() {
        def h = {
            refresh();
        }
//        def rf = [documentid: entity.objid];
        return Inv.lookupOpener( "document_redflag:create", [entity: entity, handler: h] );
    }
            
    def openRedflag() {
        if(!selectedItem) throw new Exception('Please select an item');
        if(!service.isallowedtoopen(OsirisContext.env.USERID,selectedItem.filedby.objid)) throw new Exception('You are not allowed to edit this red flag.');
                
        def h = {
            refresh();
        }
        return Inv.lookupOpener( "document_redflag:open", [entity:selectedItem, handler: h] );
    }
            
    def resolveRedflag() {
        if(!selectedItem) throw new Exception('Please select an item');
        def h = {
            refresh();
        }
        return Inv.lookupOpener( "document_redflag:resolve", [entity:selectedItem, handler: h] );
    }
            
}