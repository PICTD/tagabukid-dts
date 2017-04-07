import com.rameses.rcp.annotations.* 
import com.rameses.rcp.common.* 
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import com.rameses.gov.etracs.rpt.faas.change.ui.*;


public class ModifyDocumentDetailController
{
    
    @Binding
    def binding;
    
    @Caller
    def caller
    
    @Service("TagabukidSubayDocumentService")
    def svc;
    
    String title = 'Modify Document Detail';
    def entity;
    
    void init(){
        entity = [
            objid: caller.entity.objid,
            din : caller.entity.din,
            documenttype : caller.entity.documenttype,
            title : caller.entity.title,
            description: caller.entity.description,
            isoffline : caller.entity.isoffline,
            author : caller.entity.author,
            child:[]
        ];
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
    
    def listHandler = [
        fetchList : { return entity.child },
        onRemoveItem : {
            if (MsgBox.confirm('Delete item?')){                
                entity.child.remove(it)
                listHandler?.load();
                return true;
            }
            return false;
        },
        onColumnUpdate:{item,colName ->
            entity.child.each{y ->
                if (item.din == y.din){
                    y.message = item.remarks
                }
            }
        }
    ] as BasicListModel
    
     def save(){
        if( MsgBox.confirm('Update existing Document with new information?') ) {
            svc.modifygeneralinfo(entity);
            caller.entity.title = entity.title
            caller.entity.description = entity.description
            caller.entity.author = entity.author
            caller.entity.documenttype = entity.documenttype
            caller.entity.child = entity.child
            caller?.refreshForm();
            
            MsgBox.alert("Document Information Updated");
            return '_close';
        }
    }
}
       