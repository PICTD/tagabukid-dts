import com.rameses.rcp.common.*
import com.rameses.rcp.annotations.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*

class ParameterArchiveController 
{
    @Binding
    def binding;
         
    def entity;
    def item;
         
    @PropertyChangeListener
    def listener = [
              
    ]
         
    void init(){
        entity.org = [:];
    }
         
    void refresh() {
        if(binding) binding.refresh();
    } 
         
    def getLookupCabinet(){
        return Inv.lookupOpener('cabinet:lookup',[
                onselect :{
                    entity.cabinet = it
                },
            ])
    }
          
}  