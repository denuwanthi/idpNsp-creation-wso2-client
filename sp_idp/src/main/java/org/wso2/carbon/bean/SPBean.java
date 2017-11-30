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

package org.wso2.carbon.bean;


import org.wso2.carbon.identity.application.common.model.xsd.ClaimConfig;
import org.wso2.carbon.identity.application.common.model.xsd.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.oauth.stub.dto.OAuthConsumerAppDTO;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderDTO;

import java.util.List;

/**
 * Bean class to hold service provider data
 */
public class SPBean {
    private String applicationName;
    private String description;

    private ClaimConfig claimConfig;

    private LocalAndOutBoundAuthConfigBean localAndOutBoundAuthConfigBean = new LocalAndOutBoundAuthConfigBean();

    private OAuthConsumerAppDTO oAuthConsumerAppDTO;

    private SAMLSSOServiceProviderDTO samlssoServiceProviderDTO;

    private List<InboundAuthenticationRequestConfig> inboundAuthenticationRequestConfigs;

    private List<String> idpNameList;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClaimConfig getClaimConfig() {
        return claimConfig;
    }

    public void setClaimConfig(ClaimConfig claimConfig) {
        this.claimConfig = claimConfig;
    }

    public LocalAndOutBoundAuthConfigBean getLocalAndOutBoundAuthConfigBean() {
        return localAndOutBoundAuthConfigBean;
    }

    public void setLocalAndOutBoundAuthConfigBean(LocalAndOutBoundAuthConfigBean localAndOutBoundAuthConfigBean) {
        this.localAndOutBoundAuthConfigBean = localAndOutBoundAuthConfigBean;
    }

    public OAuthConsumerAppDTO getoAuthConsumerAppDTO() {
        return oAuthConsumerAppDTO;
    }

    public void setoAuthConsumerAppDTO(OAuthConsumerAppDTO oAuthConsumerAppDTO) {
        this.oAuthConsumerAppDTO = oAuthConsumerAppDTO;
    }

    public List<InboundAuthenticationRequestConfig> getInboundAuthenticationRequestConfigs() {
        return inboundAuthenticationRequestConfigs;
    }

    public void setInboundAuthenticationRequestConfigs(List<InboundAuthenticationRequestConfig> inboundAuthenticationRequestConfigs) {
        this.inboundAuthenticationRequestConfigs = inboundAuthenticationRequestConfigs;
    }

    public List<String> getIdpNameList() {
        return idpNameList;
    }

    public void setIdpNameList(List<String> idpNameList) {
        this.idpNameList = idpNameList;
    }

    public SAMLSSOServiceProviderDTO getSamlssoServiceProviderDTO() {
        return samlssoServiceProviderDTO;
    }

    public void setSamlssoServiceProviderDTO(SAMLSSOServiceProviderDTO samlssoServiceProviderDTO) {
        this.samlssoServiceProviderDTO = samlssoServiceProviderDTO;
    }
}
