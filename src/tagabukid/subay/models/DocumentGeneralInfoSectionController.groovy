import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*
import tagabukid.utils.*;
import com.rameses.common.*;
        
class  DocumentGeneralInfoSectionController  {
    @Binding
    def binding;
    
    @Caller
    def caller
    
    @Service("DateService")
    def dtsvc
    
    @Service("TagabukidSubayDocumentService")
    def svc;
            
    @Service("TagabukidSubayCabinetService") 
    def subaycabinetService;
         
    @Service("TagabukidSubayTransactionService")
    def txnsvc;
    
    String title = "General Info";
    String entityName = "subaydocument:generalinfo";
            
    def entity;
    def attachmentSelectedItem;
    def selectedItem;
    def isoffline;
    def isowner;
    
    def attachmentListHandler = [
        fetchList : { return entity.attachments },
    ] as BasicListModel
            
    void init(){
        isoffline = (entity.isoffline == 1 ? true : false);
        isowner = svc.checkDocumentOwner(entity.dininventoryid)
        loadAttachments()
        listHandler?.load();
    }
    void loadAttachments(){
        entity.attachments = [];
                
        try{
            entity.attachments = TagabukidDBImageUtil.getInstance().getImages(entity?.objid);
        }
        catch(e){
            println 'Load Attachment error ============';
            e.printStackTrace();
        }
        attachmentListHandler?.load();
    }
            
    def addAttachment(){
        return InvokerUtil.lookupOpener('upload:attachment', [
                entity : entity,
                afterupload: {
                    loadAttachments();
                }
            ]);
    }

    def viewAttachment(){
        if (!attachmentSelectedItem) return null;

        if (attachmentSelectedItem.extension.contains("pdf")){
            return InvokerUtil.lookupOpener('attachmentpdf:view', [
                    entity : attachmentSelectedItem,
                ]); 
        }else{
            return InvokerUtil.lookupOpener('attachment:view', [
                    entity : attachmentSelectedItem,
                ]); 
        }

    }
    def listHandler = [
        fetchList : { return entity.child },
    ] as EditorListModel
         
    def docinfoListHandler = [
       fetchList: { o->
            def list = [];
            if( entity.docinfos ) {
                return entity.docinfos;
            }
            return list;
        }
    ] as BasicListModel;
    
    def print() {
        def op = Inv.lookupOpener( "dts:din", [entity: entity] );
        op.target = 'self';
        return op;
    }

    def showChild() {
        if( !sezlectedItem.objid )
        throw new Exception("No parent document");
        def child = [:]
        child.objid = selectedItem.objid
        child.taskid = selectedItem.taskid   
        return Inv.lookupOpener( "subaydocument:open", [entity: child] ); 
    }
    
    def popupChangeInfo(def inv) {
        def popupMenu = new PopupMenuOpener();
        def list = InvokerUtil.lookupOpeners( inv.properties.category).findAll{
            def vw = it.properties.visibleWhen;
            return  ((!vw)  ||  ExpressionResolver.getInstance().evalBoolean( vw, [entity:getEntity(), orgid:OsirisContext.env.ORGID] ));
        }
        list.each{
            popupMenu.add( it );
        }
        return popupMenu;
    }
            
    void refreshForm(){
        isoffline = false;
        binding.refresh('entity.*');
        binding.refresh();
        listHandler.reload();
    }  
    
    def refresh(){
        //listModel.reload()
        def newlogs = svc.lookupNode([refid:entity.objid,taskid:entity.taskid])
          
        if (newlogs.size == 1){
            entity = svc.open(newlogs[0]);
        }else if (newlogs.size > 1){
            return Inv.lookupOpener('node:lookup',[
                    entity: newlogs,
                    onselect :{
                        entity = svc.open(it)
                    }
                ])
        }
        
        isoffline = (entity.isoffline == 1 ? true : false);
        isowner = svc.checkDocumentOwner(entity.dininventoryid)
        loadAttachments()
        listHandler?.load();
        binding.refresh();  
        caller.entity = entity;
        caller.reloadSections();
    }
    
    def transferParent() {
        return InvokerUtil.lookupOpener( "cabinet:lookup", [
            onselect: { o->

                if (o.objid == entity.objid){
                     throw new Exception("Cannot Transfer " + entity.title  + " to " + entity.title);
                }
                else if(o.type == 'folder' && entity.type == 'cabinet'){
                     throw new Exception("Cannot Transfer a cabinet to a folder");
                }
                if( MsgBox.confirm('You are about to transfer this folder to another cabinet Continue?') ) {
                    subaycabinetService.removeEntity( [objid:entity.objid] );
//                    entity.parentid = o.objid;
//                    entity.parent = o;
                    archiveDocument(o);
                    refresh();
                   
                }
            }
        ]);
    }
    
     void archiveDocument(o){
//        throw new Exception("HERE");
        def arch = [:];
        def document = [];
//        def entity = service.open([barcodeid: entity.din]);
        arch.cabinet = [objid:o.objid]
        entity.message =  "DOCUMENT ARCHIVED AT " + txnsvc.getUserOrg(OsirisContext.env.USERID).org.name.toUpperCase() + ". " + o.type.toUpperCase() + " " +  o.title + " BY " + OsirisContext.env.FULLNAME.toUpperCase();
        document << entity;
        arch.txndate = dtsvc.getServerDate();
        arch.preparedbyname = OsirisContext.env.FULLNAME;
        arch.document = document;
        arch.mode = 'archived';
        txnsvc.processDocument(arch);
    }
    
}