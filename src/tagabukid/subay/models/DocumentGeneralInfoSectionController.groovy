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
    
//    @Script("TagabukidSubayDocumentInfoUtil")
//    def docinfo
    
    @Service("TagabukidSubayDocumentService")
    def svc;
            
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
//        binding.refresh('entity.*');
        binding.refresh();
        listHandler.reload();
    }   
    
}