import com.rameses.rcp.annotations.*
import com.rameses.rcp.common.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import com.rameses.common.*;
import java.rmi.server.*
import com.rameses.util.*;
import com.rameses.gov.etracs.bpls.business.*;

class  DocumentInfoController {

    @Service("DocumentMasterService")
    def service;

    @FormId
    def formId
    
    @FormTitle
    def title
    
    @Binding
    def binding;
    
    def entityName = "documentform";
    def entity;
   
    def sections;
    def currentSection;
    def barcodeid;
    def startstep;
    
    
    def openByDIN() {
        MsgBox.alert( 'open business by DIN '+ barcodeid ); 
    }
    
    void open() {
        entity = service.open( [objid: entity.objid] );
        title = entity.name + '(' + entity.din + ')';
        loadSections();
        formId = entity.objid;
    }
    
    void reloadSection() {
        binding.refresh("subform");
    }
    
    void loadSections() {
        sections = InvokerUtil.lookupOpeners( "document:section", [entity: entity ] ).findAll {
            def vw = it.properties.visibleWhen;
            return  ((!vw)  ||  ExpressionResolver.getInstance().evalBoolean( vw, [entity:entity] ));
        }
        if( sections.size()>0 ) {
            currentSection = sections[0];
        }  
    }
    
    void reloadCurrentSection() {
        MsgBox.alert( currentSection.name );
    }
    
    def showEditMenu() {
        def h = { t->
            if(t) open();
            binding.refresh();
        }
        def list = Inv.lookupOpeners( "business:change", [entity:entity, handler:h] );
        def pop = new PopupMenuOpener();
        list.each {
            pop.add( it );
        }
        return pop;
    }
    
    def showOwner() {
        if( !entity.owner?.objid )
        throw new Exception("No owner assigned");
        String stype = (entity.orgtype == 'SING') ?'individualentity':'juridicalentity';
        return Inv.lookupOpener( stype + ":open", [entity: entity.owner] ); 
    }
    
    def createSMS() {
        def phoneno = entity.mobileno;
        if(!phoneno) phoneno = entity.owner?.mobileno;
        return Inv.lookupOpener('business_sms:create', [phoneno: phoneno]); 
    }
}