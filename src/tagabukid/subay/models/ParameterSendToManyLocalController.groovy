
import com.rameses.rcp.common.*
import com.rameses.rcp.annotations.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*

class ParameterSendToManyLocalController
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
        return Inv.lookupOpener('userorg:lookup',[
                onselect :{
                    entity.org = it.org
                },
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
        def data = listtofilter.find{it.org.objid == item.org.objid }
        if (data)
        throw new Exception("Duplicate item is not allowed.")
    }   
}  
