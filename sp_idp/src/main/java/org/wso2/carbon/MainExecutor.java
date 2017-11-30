package org.wso2.carbon;
import java.util.logging.*;

public class MainExecutor {
    private static Logger logger = Logger.getLogger("org.wso2.carbon");

    public static void main(String args[]) throws Exception {

// Here, arg[9] gives whether update or add and arg[7] gives whether IDP or SP

        if("Add".equals(args[9])) {

            if ("SP".equals(args[7])) {
                AutomateSPExecutor spExecutor = new AutomateSPExecutor();
                spExecutor.execute(args);
            } else if ("IDP".equals(args[7])) {
                AutomateIDPExecutor idpExecutor = new AutomateIDPExecutor();
                idpExecutor.execute(args);
            } else {
                logger.log(Level.INFO,"Invalid arguments");
            }
        }

        else if("Update".equals(args[9]))
        {
            if ("SP".equals(args[7])) {
                AutomateSPExecutor spExecutor = new AutomateSPExecutor();
                spExecutor.execute(args);
            } else if ("IDP".equals(args[7])) {
                AutomateIDPExecutor idpExecutor = new AutomateIDPExecutor();
                idpExecutor.execute(args);
            } else {
                logger.log(Level.INFO,"Invalid arguments");
            }
        }

        else
        {
            logger.log(Level.INFO,"Invalid arguments");
        }




    }

}
