package etracs.training;

import com.rameses.osiris2.test.OsirisTestPlatform;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            Map env = new HashMap();
            env.put("app.host", "localhost:8072");
            env.put("app.context", "etracs25");
            env.put("app.cluster", "osiris3");
        
            Map profile = new HashMap();
            profile.put("CLIENTTYPE", "desktop");
            profile.put("USERID", "USR23bf1d10:14e9bbd2583:-7d5a");
            profile.put("USER", "ADMIN");
            profile.put("FULLNAME", "ADMINISTRATOR");
            profile.put("ORGID", "059");
            profile.put("ORGCODE", "059");
            profile.put("ORGNAME", "BUKIDNON");
            profile.put("ORGCLASS", "PROVINCE");
            profile.put("MACADDRESS", "6C-3B-E5-7F-F6-E7");
            Map roles = new HashMap();
            roles.put("DTS.MASTER",null);
            roles.put("DTS.SHARED",null);
            roles.put("DTS.BTACS",null);
            
            OsirisTestPlatform.runTest(env, roles, profile);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
       
        
    }
}
