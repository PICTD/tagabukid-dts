
import com.rameses.rcp.common.*
import com.rameses.rcp.annotations.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*

class SendParametersToManyLocalController
{
    @Binding
    def binding;
    
    def entity;
    def item;
    def selectedItem;
    
    void init(){
        entity.destinations = []
        entity.assignee = [:]
    }
    
    void refresh() {
        if(binding) binding.refresh();
    } 
    
    def getLookupOrg(){
        return Inv.lookupOpener('hrisorg:lookup',[
                onselect :{
//                    entity.destinations << it;
//                    listHandler.reload(); 
                    selectedItem.OrgUnitId = it.OrgUnitId
                    selectedItem.name = it.Entity.Name
                    selectedItem.code = it.Entity.AcronymAbbreviation
                }
            ])
    }
    def getLookupAssignee(){
        return Inv.lookupOpener('etracsuserorg:lookup',[
                onselect :{
                    entity.assignee = it;
                },
            ])
    }
    def listHandler = [
        fetchList: { entity.destinations },
        onRemoveItem : {
            if (MsgBox.confirm('Delete item?')){                
                entity.destinations.remove(it)
                listHandler?.load();
                return true;
            }
            return false;
        },
        onAddItem : {
            entity.destinations << it;
        },
        validate:{li->
            def item=li.item;
            checkDuplicate(entity.destinations,item);
        }
    ] as EditorListModel
    
    
    void checkDuplicate(listtofilter,item){
        def data = listtofilter.find{it.OrgUnitId == item.OrgUnitId }
        if (data)
        throw new Exception("Duplicate item is not allowed.")
    }   
}  
