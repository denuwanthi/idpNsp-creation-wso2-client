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
import org.wso2.carbon.bean.SPBean;
import org.wso2.carbon.util.XMLFileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.logging.*;
import java.io.File;
import java.util.List;

/**
 * Main method class to execute service provider adding
 */
public class AutomateSPExecutor {
    private static Logger logger = Logger.getLogger("org.wso2.carbon");

    public static void main(String[] args) throws Exception {

        setKeyStoreProperties(args);
        //        String backendURL = args[3];
        String backendURL = "https://localhost:9443/services/";

        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);

        logger.log(Level.INFO,"login to IS begins.................");
        //        String sessionCookie = authenticatorClient.login(args[0], args[1], args[2]);
        String sessionCookie = authenticatorClient.login("admin", "admin", "localhost");
        logger.log(Level.INFO,"login to IS ends.................");

        //        File file  = new File(args[6]);
        File file = new File("service-provider.xml"); //service-provider.xml
        //        XMLFileReader reader = new XMLFileReader(args[4]);
        XMLFileReader reader = new XMLFileReader("adfs.prod.crt");
        logger.log(Level.INFO,"Read xml file begins.................");
        Document doc = reader.readXMl(file);
        List<SPBean> spBeansList = reader.getSPBeans(doc);
        logger.log(Level.INFO,"Read xml file ends..................");
        AddSPClient addSPClient = new AddSPClient(backendURL, sessionCookie);
        logger.log(Level.INFO,"Calling web service to add service providers begins........");
        addSPClient.addSPs(spBeansList);
        logger.log(Level.INFO,"Calling web service to add service providers ends........");
        logger.log(Level.INFO,"Calling web service to add oauth apps begins........");
        for (SPBean spBean : spBeansList) {
            AddOauthConfigClient addOauthConfigClient = new AddOauthConfigClient(backendURL, sessionCookie);
            addOauthConfigClient.addOauth2Config(spBean);

            AddSAMLSSOClient samlssoClient = new AddSAMLSSOClient(backendURL, sessionCookie);
            samlssoClient.addSAMLSSOConfig(spBean);
        }
        logger.log(Level.INFO,"Calling web service to add oauth apps ends........");
    }

    public void execute(String[] args) throws Exception {
        setKeyStoreProperties(args);
        String backendURL = args[3];

        AuthenticatorClient authenticatorClient = new AuthenticatorClient(backendURL);

        logger.log(Level.INFO,"login to IS begins.................");
        String sessionCookie = authenticatorClient.login(args[0], args[1], args[2]);
        logger.log(Level.INFO,"login to IS ends.................");

        File file = new File(args[4]); //{path}/service-provider.xml
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        XMLFileReader reader = new XMLFileReader(args[4]);
        logger.log(Level.INFO,"Read xml file begins.................");
        Document doc = dBuilder.parse(file);
        List<SPBean> spBeansList = reader.getSPBeans(doc);
        logger.log(Level.INFO,"Read xml file ends..................");
        AddSPClient addSPClient = new AddSPClient(backendURL, sessionCookie);
        logger.log(Level.INFO,"Calling web service to add service providers begins........");
        addSPClient.addSPs(spBeansList);
        logger.log(Level.INFO,"Calling web service to add service providers ends........");
        logger.log(Level.INFO,"Calling web service to add oauth apps begins........");
        for (SPBean spBean : spBeansList) {
            AddOauthConfigClient addOauthConfigClient = new AddOauthConfigClient(backendURL, sessionCookie);
            addOauthConfigClient.addOauth2Config(spBean);

            AddSAMLSSOClient samlssoClient = new AddSAMLSSOClient(backendURL, sessionCookie);
            samlssoClient.addSAMLSSOConfig(spBean);
        }
        logger.log(Level.INFO,"Calling web service to add oauth apps ends........");
    }

    public static void setKeyStoreProperties(String args[]) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", args[5]);
        System.setProperty("javax.net.ssl.trustStorePassword", args[8]);
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }
}
