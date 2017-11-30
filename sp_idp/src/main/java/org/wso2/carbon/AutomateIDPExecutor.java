/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon;

import org.w3c.dom.Document;
import org.wso2.carbon.bean.IDPBean;
import org.wso2.carbon.util.XMLFileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Main method class used to add Identity providers
 */
public class AutomateIDPExecutor {

    private static Logger logger = Logger.getLogger("org.wso2.carbon");

    public static void main(String[] args) throws Exception {
        setKeyStoreProperties(args);


        //        String backendURL = args[3];
        String backendURL = "https://localhost:9443/services/";

        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);

        logger.log(Level.INFO,"Login to IS begins.................");
        //        String sessionCookie = authenticatorClient.login(args[0], args[1], args[2]);
        String sessionCookie = authenticatorClient.login("admin", "admin", "localhost");
        logger.log(Level.INFO,"Login to IS ends.................");

        //        File file  = new File(args[6]); //service-provider.xml
        File file = new File("identity-provider.xml"); //service-provider.xml
        //        XMLFileReader reader = new XMLFileReader(args[4]);
        XMLFileReader reader = new XMLFileReader("adfs.prod.crt");
        logger.log(Level.INFO,"Read xml file begins.................");
        Document doc = reader.readXMl(file);
        List<IDPBean> idpBeansList = reader.getIDPBeans(doc);
        logger.log(Level.INFO,"Read xml file ends..................");

        if("Add".equals(args[9]))
        {
            AddIDPClient addIDPClient = new AddIDPClient(backendURL, sessionCookie);
            addIDPClient.addIDPs(idpBeansList);
        }
        else if("Update".equals(args[9]))
        {
            logger.log(Level.INFO,"Calling update method...IDP...........");
            UpdateIDPClient updateIDPClient = new UpdateIDPClient(backendURL, sessionCookie);
            updateIDPClient.UpdateIDPs(idpBeansList, args[9]);
        }
        else
        {

        }
    }

    public void execute(String[] args) throws Exception {
        setKeyStoreProperties(args);


        String backendURL = args[3];

        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);

        logger.log(Level.INFO,"Login to IS begins.................");
        String sessionCookie = authenticatorClient.login(args[0], args[1], args[2]);
        logger.log(Level.INFO,"Login to IS ends.................");

        File file = new File(args[4]); //{path}/identity-provider.xml
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        XMLFileReader reader = new XMLFileReader(args[4]);
        logger.log(Level.INFO,"Read xml file begins.................");
        Document doc = dBuilder.parse(file);
        List<IDPBean> idpBeansList = reader.getIDPBeans(doc);
        logger.log(Level.INFO,"Read xml file ends..................");

         if("Add".equals(args[9])) {
             logger.log(Level.INFO,"Calling add method...IDP...............");
             AddIDPClient addIDPClient = new AddIDPClient(backendURL,sessionCookie);
             addIDPClient.addIDPs(idpBeansList);
             AddSPClient addSPClient = new AddSPClient(backendURL, sessionCookie);
             addSPClient.updateOutboundProvisioningConfigForLocalSp(idpBeansList);
         } else if("Update".equals(args[9])) {
             logger.log(Level.INFO,"Calling update method...IDP...............");
             UpdateIDPClient updateIDPClient = new UpdateIDPClient(backendURL, sessionCookie);
             updateIDPClient.UpdateIDPs(idpBeansList, args[10]);
             AddSPClient addSPClient = new AddSPClient(backendURL, sessionCookie);
             addSPClient.updateOutboundProvisioningConfigForLocalSp(idpBeansList, args[10]);
         }


    }

    public static void setKeyStoreProperties(String args[]) throws Exception {
        System.setProperty("javax.net.ssl.trustStore",args[5]);
        System.setProperty("javax.net.ssl.trustStorePassword", args[8]);
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

}
