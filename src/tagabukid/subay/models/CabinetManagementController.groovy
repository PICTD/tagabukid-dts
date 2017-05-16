/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tagabukid.subay.models

import com.rameses.osiris2.common.ExplorerViewController;

public abstract class CabinetManagementController extends ExplorerViewController {
    
    public abstract String getServiceName();
    public abstract Object getSubaycabinetService();
    public abstract String getTitle();
    
    public String getPrefixId() {
        return "CAB";
    }
    public String getDefaultFileType() {
        return "subaycabinet";
    }
    
    public String getContext() {
        return "subaycabinet";
    }    

//    void sync() {
//        if(! MsgBox.confirm("This will update your current records. Continue? ")) return;
//        getAccountService().syncFromCloud();
//    }
    
}