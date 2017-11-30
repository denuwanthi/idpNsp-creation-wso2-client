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


import org.wso2.carbon.bean.IDPBean;
import org.wso2.carbon.identity.application.common.model.idp.xsd.FederatedAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.idp.xsd.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.idp.xsd.ProvisioningConnectorConfig;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityProviderManagementExceptionException;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceStub;
import org.wso2.carbon.util.AuthenticateStubUtil;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client class to add identity providers by calling service stubs
 */
public class AddIDPClient {

    private IdentityProviderMgtServiceStub idpMgtStub;
    private final String serviceName = "IdentityProviderMgtService";
    private IdentityApplicationManagementServiceStub idpAppMgtStub;
    private final String spServiceName = "IdentityApplicationManagementService";
    private static Logger logger = Logger.getLogger("org.wso2.carbon");
    public AddIDPClient(String backendUrl, String sessionCookie) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpMgtStub = new IdentityProviderMgtServiceStub(endPoint);

        String spEndpoint = backendUrl + spServiceName;
        idpAppMgtStub = new IdentityApplicationManagementServiceStub(spEndpoint);

        AuthenticateStubUtil.authenticateStub(sessionCookie, idpMgtStub);
        AuthenticateStubUtil.authenticateStub(sessionCookie, idpAppMgtStub);
    }

    public AddIDPClient(String backendUrl, String userName, String password) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpMgtStub = new IdentityProviderMgtServiceStub(endPoint);

//        String spEndpoint = backendUrl + spServiceName;
//        idpAppMgtStub = new IdentityApplicationManagementServiceStub(spEndpoint);

        AuthenticateStubUtil.authenticateStub(userName, password, idpMgtStub);
//        AuthenticateStubUtil.authenticateStub(userName, password, idpAppMgtStub);
    }

    /**
     * Fill the identity provider object from idp bean and call the addIdp method to add the identity provider
     *
     * @param idpBeansList
     * @throws RemoteException
     */
    public void addIDPs(List<IDPBean> idpBeansList)
            throws RemoteException, IdentityProviderMgtServiceIdentityProviderManagementExceptionException {
        for (IDPBean idpBean : idpBeansList) {

            IdentityProvider identityProvider = new IdentityProvider();
            identityProvider.setEnable(idpBean.getenable());
            identityProvider.setIdentityProviderName(idpBean.getIdentityProviderName());
            identityProvider.setDisplayName(idpBean.getDisplayName());
            identityProvider.setIdentityProviderDescription(idpBean.getIdentityProviderDescription());
            identityProvider.setAlias(idpBean.getAlias());
            identityProvider.setCertificate(idpBean.getCertificate());


            IdentityProvider identityProviderUpdate = new IdentityProvider();
            identityProviderUpdate.setEnable(idpBean.getenable());
            identityProviderUpdate.setIdentityProviderName(idpBean.getIdentityProviderName());
            identityProviderUpdate.setDisplayName(idpBean.getDisplayName());
            identityProviderUpdate.setIdentityProviderDescription(idpBean.getIdentityProviderDescription());

            identityProviderUpdate.setAlias(idpBean.getAlias());
            identityProviderUpdate.setCertificate(idpBean.getCertificate());


            //setting claims here
            logger.log(Level.INFO,"Setting claim configs for idp begins......");
            identityProvider.setClaimConfig(idpBean.getClaimConfig());
            identityProviderUpdate.setClaimConfig(idpBean.getClaimConfig());
            logger.log(Level.INFO,"Setting claim configs for idp ends......");


            //saml2ssoConfiguration
            logger.log(Level.INFO,"Setting SAML2 SSO configuration for idp begins.......");
            identityProvider.setDefaultAuthenticatorConfig(idpBean.getFederatedAuthenticatorConfig());
            identityProviderUpdate.setDefaultAuthenticatorConfig(idpBean.getFederatedAuthenticatorConfig());
            identityProvider.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{idpBean.getFederatedAuthenticatorConfig()});
            identityProviderUpdate.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{idpBean.getFederatedAuthenticatorConfig()});
            logger.log(Level.INFO,"Setting SAML2 SSO configuration for idp ends.......");

            //setting justInTime provisioning
            logger.log(Level.INFO,"Setting just in time provisioning configs for idp begins......");
            identityProvider.setJustInTimeProvisioningConfig(idpBean.getJustInTimeProvisioningConfig());
            identityProviderUpdate.setJustInTimeProvisioningConfig(idpBean.getJustInTimeProvisioningConfig());
            logger.log(Level.INFO,"Setting just in time provisioning configs for idp ends......");

            //adding provisioning connector configs

            if (idpBean.getProvisioningConnectorConfig().getName() != null) {
                identityProvider.setProvisioningConnectorConfigs(new ProvisioningConnectorConfig[]{idpBean.getProvisioningConnectorConfig()});
                identityProviderUpdate.setProvisioningConnectorConfigs(new ProvisioningConnectorConfig[]{idpBean.getProvisioningConnectorConfig()});
            }

            logger.log(Level.INFO,"setting Role based configs...........");
            if (idpBean.getPermissionAndRoleConfig() != null) {
                identityProviderUpdate.setPermissionAndRoleConfig(idpBean.getPermissionAndRoleConfig());

            }
            logger.log(Level.INFO,"Setting provisioning connector configs for idp ends...........");
            logger.log(Level.INFO,"Setting provisioning connector configs for idp ends...........");

            //create new IDP.
            idpMgtStub.addIdP(identityProvider);
            //update role mapping in IDP.
            idpMgtStub.updateIdP(idpBean.getIdentityProviderName(), identityProviderUpdate);


            logger.log(Level.INFO,"Calling web service to add identity providers ends........");

        }


    }




}
