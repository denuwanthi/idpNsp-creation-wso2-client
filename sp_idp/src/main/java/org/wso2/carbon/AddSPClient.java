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

import org.wso2.carbon.bean.FederatedAuthenticatorConfigBean;
import org.wso2.carbon.bean.IDPBean;
import org.wso2.carbon.bean.LocalAuthenticatorConfigBean;
import org.wso2.carbon.bean.SPBean;
import org.wso2.carbon.identity.application.common.model.xsd.AuthenticationStep;
import org.wso2.carbon.identity.application.common.model.xsd.FederatedAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.xsd.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.xsd.LocalAndOutboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.xsd.LocalAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.xsd.OutboundProvisioningConfig;
import org.wso2.carbon.identity.application.common.model.xsd.ProvisioningConnectorConfig;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityProviderManagementExceptionException;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceStub;
import org.wso2.carbon.util.AuthenticateStubUtil;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client class to add service providers by calling stubs
 */
public class AddSPClient {

    private IdentityApplicationManagementServiceStub idpAppMgtStub;
    private final String serviceName = "IdentityApplicationManagementService";
    private IdentityProviderMgtServiceStub idpMgtStub;
    private final String idpMgtServiceName = "IdentityProviderMgtService";
    private static Logger logger = Logger.getLogger("org.wso2.carbon");
    private final String localSPName = "wso2carbon-local-sp";

    public AddSPClient(String backendUrl, String sessionCookie) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpAppMgtStub = new IdentityApplicationManagementServiceStub(endPoint);
        String idpMgtEndPoint = backendUrl + idpMgtServiceName;
        idpMgtStub = new IdentityProviderMgtServiceStub(idpMgtEndPoint);


        AuthenticateStubUtil.authenticateStub(sessionCookie, idpAppMgtStub);
        AuthenticateStubUtil.authenticateStub(sessionCookie, idpMgtStub);
    }

    public AddSPClient(String backendUrl, String userName, String password) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpAppMgtStub = new IdentityApplicationManagementServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, idpAppMgtStub);
    }

    /**
     * Add method for service providers
     *
     * @param spBeanList
     * @throws Exception
     */
    public void addSPs(List<SPBean> spBeanList) throws Exception {
        for (SPBean spBean : spBeanList) {
            ServiceProvider serviceProvider = new ServiceProvider();
            String spName = spBean.getApplicationName();
            serviceProvider.setApplicationName(spName);
            serviceProvider.setDescription(spBean.getDescription());

            idpAppMgtStub.createApplication(serviceProvider);

            ServiceProvider toBeUpdateSP = idpAppMgtStub.getApplication(spName);

            //setting claims here
            logger.log(Level.INFO, "Setting claim config for sp begins........");
            toBeUpdateSP.setClaimConfig(spBean.getClaimConfig());
            logger.log(Level.INFO, "Setting claim config for sp ends........");


            idpAppMgtStub.updateApplication(toBeUpdateSP);

            logger.log(Level.INFO, "Setting provisioning configs for sp begins...........");
            setProvisioningConfigForSP(spName, spBean.getIdpNameList());
            logger.log(Level.INFO, "Setting provisioning configs for sp ends...........");

            logger.log(Level.INFO, "Setting provisioning configs for resident sp begins...........");
            //setProvisioningConfigForLocalSP("wso2carbon-local-sp", spBean.getIdpNameList());
            logger.log(Level.INFO, "Setting provisioning configs for resident sp ends...........");

            logger.log(Level.INFO, "Setting authentication steps for sp begins..........");
            setAuthenticationStepsToSP(spBean);
            logger.log(Level.INFO, "Setting authentication steps for sp ends..........");
        }

    }

    public void updateOutboundProvisioningConfigForLocalSp(List<IDPBean> idpBeansList) {
        List<String> idpsList = new ArrayList<String>();
        try {
            // Get all the available outbound provisioning identity providers available in the current local sp.
            IdentityProvider[] idpArray = idpAppMgtStub.getApplication(localSPName)
                    .getOutboundProvisioningConfig().getProvisioningIdentityProviders();
            if (idpArray != null) {
                // If the there are already available IDPs set them in list of IDPs to add
                for (IdentityProvider idp : idpArray) {
                    idpsList.add(idp.getIdentityProviderName());
                }
            }
        } catch (RemoteException e) {
            logger.log(Level.WARNING, "Unable to call the stub");
        } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            logger.log(Level.WARNING, "ApplicationMangementService does not work.");
        }
        for (IDPBean idp : idpBeansList) {
            idpsList.add(idp.getIdentityProviderName());
        }
        try {
            setProvisioningConfigForLocalSP("wso2carbon-local-sp", idpsList);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Idps are cannot be set as OutboundProvisioningConfigs");
        }
    }

    public void updateOutboundProvisioningConfigForLocalSp(List<IDPBean> idpBeansList, String idpName) {
        List<String> idpsList = new ArrayList<String>();
        try {
            // Get all the available outbound provisioning identity providers available in the current local sp.
            IdentityProvider[] idpArray = idpAppMgtStub.getApplication(localSPName)
                    .getOutboundProvisioningConfig().getProvisioningIdentityProviders();
            if (idpArray != null) {
                // If the there are already available IDPs set them in list of IDPs to add
                for (IdentityProvider idp : idpArray) {
                    // Remove the give IDP
                    if (idp.getIdentityProviderName().equals(idpName)) {
                        idpsList.remove(idp.getIdentityProviderName());
                    } else {
                        idpsList.add(idp.getIdentityProviderName());
                    }
                }
            }
        } catch (RemoteException e) {
            logger.log(Level.WARNING, "Unable to call the stub");
        } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
            logger.log(Level.WARNING, "ApplicationMangementService does not work.");
        }
        for (IDPBean idp : idpBeansList) {
            idpsList.add(idp.getIdentityProviderName());
        }
        try {
            setProvisioningConfigForLocalSP("wso2carbon-local-sp", idpsList);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Idps are cannot be set as OutboundProvisioningConfigs");
        }
    }

    /**
     * Set provisioning configuration for resident provider
     *
     * @param spName      Service Provider name
     * @param idpNameList List of identity provider names
     * @throws Exception
     */
    private void setProvisioningConfigForLocalSP(String spName, List<String> idpNameList) throws Exception {
        List<IdentityProvider> idpList = new ArrayList<IdentityProvider>();
        ServiceProvider sp = idpAppMgtStub.getApplication(spName);
        OutboundProvisioningConfig outboundProvisioningConfig = new OutboundProvisioningConfig();
        for (String idpName : idpNameList) {
            IdentityProvider identityProvider = new IdentityProvider();

            identityProvider.setIdentityProviderName(idpName);

            ProvisioningConnectorConfig proCon = new ProvisioningConnectorConfig();

            if (idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs() != null) {
                proCon.setName(idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs()[0].getName());
                proCon.setBlocking(idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs()[0].getBlocking());
            } else {
                logger.log(Level.INFO, "there are no provisioning connector configs for " + idpName);
            }

            identityProvider.setDefaultProvisioningConnectorConfig(proCon);
            idpList.add(identityProvider);
        }
        outboundProvisioningConfig.setProvisioningIdentityProviders(idpList.toArray(new IdentityProvider[idpList.size()]));
        sp.setOutboundProvisioningConfig(outboundProvisioningConfig);
        idpAppMgtStub.updateApplication(sp);
    }

    /**
     * Set provisioning configuration for service provider
     *
     * @param spName      Service Provider name
     * @param idpNameList List of identity provider names
     * @throws Exception
     */
    private void setProvisioningConfigForSP(String spName, List<String> idpNameList) throws Exception {
        List<IdentityProvider> idpList = new ArrayList<IdentityProvider>();
        ServiceProvider sp = idpAppMgtStub.getApplication(spName);
        OutboundProvisioningConfig outboundProvisioningConfig = new OutboundProvisioningConfig();
        for (String idpName : idpNameList) {
            IdentityProvider identityProvider = new IdentityProvider();

            identityProvider.setIdentityProviderName(idpName);

            ProvisioningConnectorConfig proCon = new ProvisioningConnectorConfig();

            if (idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs() != null) {
                proCon.setName(idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs()[0].getName());
                proCon.setBlocking(idpMgtStub.getIdPByName(idpName).getProvisioningConnectorConfigs()[0].getBlocking());
            } else {
                logger.log(Level.INFO, "there are no provisioning connector configs for " + idpName);
            }

            identityProvider.setDefaultProvisioningConnectorConfig(proCon);
            idpList.add(identityProvider);

        }
        outboundProvisioningConfig.setProvisioningIdentityProviders(idpList.toArray(new IdentityProvider[idpList.size()]));
        sp.setOutboundProvisioningConfig(outboundProvisioningConfig);
        idpAppMgtStub.updateApplication(sp);


    }

    /**
     * Set authentication steps to service provider bean
     *
     * @param spBean Service Provider bean
     * @throws RemoteException
     * @throws IdentityApplicationManagementServiceIdentityApplicationManagementException
     */
    private void setAuthenticationStepsToSP(SPBean spBean)
            throws RemoteException, IdentityApplicationManagementServiceIdentityApplicationManagementException,
            IdentityProviderMgtServiceIdentityProviderManagementExceptionException {
        int numOfAuthSteps = spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().size();
        ArrayList<AuthenticationStep> authenticationStepsList = new ArrayList<AuthenticationStep>();

        for (int i = 0; i < numOfAuthSteps; i++) {
            AuthenticationStep step = new AuthenticationStep();
            step.setStepOrder(spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().get(i).getStepOrder());
            step.setSubjectStep(spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().get(i).isSubjectStep());
            step.setAttributeStep(spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().get(i).isAttributeStep());

            ArrayList<LocalAuthenticatorConfigBean> localAuthenticatorConfigBeanList = spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().get(i).getLocalAuthenticatorConfigList();
            List<LocalAuthenticatorConfig> localAuthList = new ArrayList<LocalAuthenticatorConfig>();

            for (int j = 0; j < localAuthenticatorConfigBeanList.size(); j++) {
                LocalAuthenticatorConfig localAuth = new LocalAuthenticatorConfig();
                localAuth.setName(localAuthenticatorConfigBeanList.get(j).getName());
                localAuth.setDisplayName(localAuthenticatorConfigBeanList.get(j).getDisplayName());
                localAuth.setEnabled(localAuthenticatorConfigBeanList.get(j).isEnabled());

                localAuthList.add(localAuth);
            }

            step.setLocalAuthenticatorConfigs(localAuthList
                    .toArray(new LocalAuthenticatorConfig[localAuthList.size()]));

            ArrayList<FederatedAuthenticatorConfigBean> federatedAuthenticatorConfigBeansList = spBean.getLocalAndOutBoundAuthConfigBean().getAuthSteps().get(i).getFederatedAuthenticatorConfigBean();
            List<IdentityProvider> federatedAuthList = new ArrayList<IdentityProvider>();

            for (int j = 0; j < federatedAuthenticatorConfigBeansList.size(); j++) {
                FederatedAuthenticatorConfig federatedAuthenticatorConfig = new FederatedAuthenticatorConfig();

                IdentityProvider identityProvider = new IdentityProvider();
                //
                String idpName = federatedAuthenticatorConfigBeansList.get(j).getIdpName();
                federatedAuthenticatorConfig.setDisplayName(idpMgtStub.getIdPByName(idpName).getFederatedAuthenticatorConfigs()[0].getDisplayName());
                federatedAuthenticatorConfig.setName(idpMgtStub.getIdPByName(idpName).getFederatedAuthenticatorConfigs()[0].getName());
                identityProvider.setIdentityProviderName(idpName);
                identityProvider.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{federatedAuthenticatorConfig});
                identityProvider.setDefaultAuthenticatorConfig(federatedAuthenticatorConfig);
                federatedAuthList.add(identityProvider);


            }

            step.setFederatedIdentityProviders(federatedAuthList.toArray(new IdentityProvider[federatedAuthList.size()]));

            authenticationStepsList.add(step);

        }

        ServiceProvider serviceProvider = idpAppMgtStub.getApplication(spBean.getApplicationName());

        serviceProvider
                .setLocalAndOutBoundAuthenticationConfig(new LocalAndOutboundAuthenticationConfig());
        serviceProvider.getLocalAndOutBoundAuthenticationConfig().setAuthenticationType("flow");
        serviceProvider.getLocalAndOutBoundAuthenticationConfig().setAuthenticationSteps(
                authenticationStepsList.toArray(new AuthenticationStep[authenticationStepsList.size()]));

        idpAppMgtStub.updateApplication(serviceProvider);


    }


}
