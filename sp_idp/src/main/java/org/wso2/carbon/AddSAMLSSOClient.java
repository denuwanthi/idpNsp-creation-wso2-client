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


import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.bean.SPBean;
import org.wso2.carbon.identity.application.common.model.xsd.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.xsd.Property;
import org.wso2.carbon.identity.application.common.model.xsd.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceIdentityException;
import org.wso2.carbon.identity.sso.saml.stub.IdentitySAMLSSOConfigServiceStub;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderDTO;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderInfoDTO;
import org.wso2.carbon.util.AuthenticateStubUtil;
import java.util.logging.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class to add SAML SSO configuration by calling service stubs
 */
public class AddSAMLSSOClient {

    private static final Log log = LogFactory.getLog(AddSAMLSSOClient.class);
    private IdentitySAMLSSOConfigServiceStub indentitySAMLSSOServiceStub;
    public IdentityApplicationManagementServiceStub idpAppMgtStub;
    private final String idpAppMgtServiceName = "IdentityApplicationManagementService";
    private final String identitySAMLSSOserviceName = "IdentitySAMLSSOConfigService";
    private static Logger logger = Logger.getLogger("org.wso2.carbon");
    
    public AddSAMLSSOClient(String backendUrl, String sessionCookie) throws Exception {
        String endPoint = backendUrl + identitySAMLSSOserviceName;
        indentitySAMLSSOServiceStub = new IdentitySAMLSSOConfigServiceStub(endPoint);
        String idpAppMgtEndPoint = backendUrl + idpAppMgtServiceName;
        idpAppMgtStub = new IdentityApplicationManagementServiceStub(idpAppMgtEndPoint);


        AuthenticateStubUtil.authenticateStub(sessionCookie, indentitySAMLSSOServiceStub);
        AuthenticateStubUtil.authenticateStub(sessionCookie, idpAppMgtStub);
    }

    public AddSAMLSSOClient(String backendUrl, String userName, String password) throws Exception {
        String endPoint = backendUrl + identitySAMLSSOserviceName;
        indentitySAMLSSOServiceStub = new IdentitySAMLSSOConfigServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, indentitySAMLSSOServiceStub);
    }

    /**
     * Add SAML SSO configuration for service provider and bind configuration with service provider
     *
     * @param spBean
     */
    public void addSAMLSSOConfig(SPBean spBean) {

        SAMLSSOServiceProviderDTO samlssoServiceProviderDTO = spBean.getSamlssoServiceProviderDTO();
        if (samlssoServiceProviderDTO != null) {

            try {
                indentitySAMLSSOServiceStub.addRPServiceProvider(samlssoServiceProviderDTO);
            } catch (RemoteException e) {
                logger.log(Level.INFO,e.getMessage());
            } catch (IdentitySAMLSSOConfigServiceIdentityException e) {
                logger.log(Level.INFO,e.getMessage());
            }


            SAMLSSOServiceProviderDTO createdSSOConfig = null;
            try {
                createdSSOConfig = getServiceProvider(samlssoServiceProviderDTO.getIssuer());
            } catch (AxisFault axisFault) {
                logger.log(Level.INFO,axisFault.getMessage());
            }


            ServiceProvider serviceProvider = null;
            try {
                serviceProvider = idpAppMgtStub.getApplication(spBean.getApplicationName());
            } catch (RemoteException e) {
                logger.log(Level.INFO,e.getMessage());
            } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
                logger.log(Level.INFO,e.getMessage());
            }

            List<InboundAuthenticationRequestConfig> authRequestList = getAuthRequestList(createdSSOConfig, spBean);
            serviceProvider.getInboundAuthenticationConfig()
                    .setInboundAuthenticationRequestConfigs(
                            authRequestList
                                    .toArray(new InboundAuthenticationRequestConfig[authRequestList
                                            .size()]));
            try {
                idpAppMgtStub.updateApplication(serviceProvider);
            } catch (RemoteException e) {
                logger.log(Level.INFO,e.getMessage());
            } catch (IdentityApplicationManagementServiceIdentityApplicationManagementException e) {
                logger.log(Level.INFO,e.getMessage());
            }

        }

    }

    /**
     * Retrieve and returns inbound authentication configuration data from SAMLSSOServiceProviderDTO
     *
     * @param createdSSOConfig
     * @param spBean
     * @return
     */
    private List<InboundAuthenticationRequestConfig> getAuthRequestList(SAMLSSOServiceProviderDTO createdSSOConfig, SPBean spBean) {
        List<InboundAuthenticationRequestConfig> authRequestList = new ArrayList<InboundAuthenticationRequestConfig>();
        if (createdSSOConfig.getIssuer() != null) {
            InboundAuthenticationRequestConfig bindRequest = new InboundAuthenticationRequestConfig();
            bindRequest.setInboundAuthKey(createdSSOConfig.getIssuer());
            bindRequest.setInboundAuthType("samlsso");
            String attrConsumServiceIndex = createdSSOConfig.getAttributeConsumingServiceIndex();
            if (attrConsumServiceIndex != null && !attrConsumServiceIndex.isEmpty()) {
                Property property = new Property();
                property.setName("attrConsumServiceIndex");
                property.setValue(attrConsumServiceIndex);
                Property[] properties = {property};
                bindRequest.setProperties(properties);
            }

            authRequestList.add(bindRequest);
        }

        return authRequestList;
    }

    /**
     * Returns Service provider for issuer
     *
     * @param issuer
     * @return service provider
     * @throws AxisFault
     */
    public SAMLSSOServiceProviderDTO getServiceProvider(String issuer) throws AxisFault {
        try {
            SAMLSSOServiceProviderInfoDTO dto = indentitySAMLSSOServiceStub.getServiceProviders();
            SAMLSSOServiceProviderDTO[] sps = dto.getServiceProviders();
            for (SAMLSSOServiceProviderDTO sp : sps) {
                if (sp.getIssuer().equals(issuer)) {
                    return sp;
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving service provider information", e);
            throw new AxisFault(e.getMessage(), e);
        }
        return null;

    }
}
