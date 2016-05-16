import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*;
import com.rameses.gov.etracs.bpls.controller.*;
import com.rameses.util.*;
import tagabukid.utils.*;
        
public class NewDocument extends PageFlowController {
            
    @Script("DocumentInfoUtil")
    def document
            
    @Service("TagabukidDTSService")
    def service
            
    @Service("DocumentTitleVerificationService")
    def verifySvc
            
    def entity;
    boolean pass;
    def searchList;
    def verificationSelectedItem;
    def attachmentSelectedItem;
    void init() {
        entity = service.initNew(entity)
        loadAttachments()
        reset();
    }
    void check() {
        searchList  = verifySvc.getList(entity.title); 
        if(searchList) {
            pass = false;
            verificationListModel.reload();
        }
        else {
            pass = true;
        }
    }
    
    void checkdin() {
        entity.din = service.verifydin(entity.din); 
        binding.refresh('entity.din');
        pass = true
    }

    def verificationListModel = [
        fetchList: { o-> return searchList;}
    ] as BasicListModel;

    void reset() {
        searchList = [];
        verificationListModel.reload();
    }

    void verify() {
        if( searchList.find{ it.weight == 100 } )
        throw new Exception("Exact document title already exists. Please choose another document title");
    }
            
    def save(){
        entity = service.create(entity)
        return entity
    }
            
    def attachmentListHandler = [
        fetchList : { return entity.attachments },
    ] as BasicListModel
            
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

    void deleteAttachment(){
        if (!attachmentSelectedItem) return;
        if (MsgBox.confirm('Delete selected Attachment?')){
            TagabukidDBImageUtil.getInstance().deleteImage(attachmentSelectedItem.objid);
            loadAttachments();
        }
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
            
    def print() {
        def op = Inv.lookupOpener( "dts:din", [entity: entity] );
        op.target = 'self';
        return op;
    }
            
    def getLookupDocumentType(){
        return Inv.lookupOpener('documenttype:lookup',[
                onselect :{
                    entity.documenttype = it;
                },
            ])
    }
            
    def getDetailHandler(){
        if(entity.documenttype.haschild){
            return InvokerUtil.lookupOpener('documentchild:form',[
                    entity: entity
                ])
        }
    }
            
          
}