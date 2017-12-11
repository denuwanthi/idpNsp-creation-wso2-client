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

package org.wso2.carbon.util;


//import org.apache.commons.codec.binary.Base64;
import org.apache.catalina.mbeans.RoleMBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.bean.*;
import org.wso2.carbon.identity.application.common.model.idp.xsd.*;
import org.wso2.carbon.identity.application.common.model.xsd.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.util.IdentityApplicationConstants;
import org.wso2.carbon.identity.oauth.stub.dto.OAuthConsumerAppDTO;
import org.wso2.carbon.identity.sso.saml.stub.types.SAMLSSOServiceProviderDTO;
import org.wso2.carbon.identity.application.common.model.idp.xsd.PermissionsAndRoleConfig;
import org.wso2.carbon.identity.application.common.model.idp.xsd.RoleMapping;
import org.wso2.carbon.identity.application.common.model.idp.xsd.LocalRole;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Responsible for reading the XML file and set the SPBean and IDPBean
 */
public class XMLFileReader {


    private String certFilePath = null;

    public XMLFileReader(String certFilePath) {
        this.certFilePath = certFilePath;
    }

    /**
     * read the xml file and return XML Document object
     *
     * @param file
     * @return Document object
     */
    public Document readXMl(File file) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;

    }

    /**
     * returns the list of idp bean list for configs in XML document
     *
     * @param document
     * @return
     */
    public ArrayList<IDPBean> getIDPBeans(Document document) {
        NodeList nList = document.getElementsByTagName("IdentityProvider");
        ArrayList<IDPBean> idpBeansList = new ArrayList<IDPBean>();
//        ArrayList<RoleMappingBean> roleBeanList = new ArrayList<>();

        for (int i = 0; i < nList.getLength(); i++) {
            IDPBean idpBean = new IDPBean();
//            IDPBean idpBeanUpdate = new IDPBean();
//            RoleMappingBean roleBean = new RoleMappingBean();
            readAndSetIDPBean(idpBean, nList, i);
//            readAndSetIDPBean(idpBeanUpdate,nList,i);
            idpBeansList.add(idpBean);
        }
        return idpBeansList;
    }

    /**
     * returns the list of sp bean list for configs in XML document
     *
     * @param document
     * @return
     */
    public ArrayList<SPBean> getSPBeans(Document document) {
        NodeList nList = document.getElementsByTagName("ServiceProvider");
        ArrayList<SPBean> spBeansList = new ArrayList<SPBean>();

        for (int i = 0; i < nList.getLength(); i++) {
            SPBean spBean = new SPBean();
            readAndSetSPBean(spBean, nList, i);
            spBeansList.add(spBean);
        }
        return spBeansList;
    }

    /**
     * Fill the IDPBean by reading XML document
     *
     * @param idpBean
     */
    public void readAndSetIDPBean(IDPBean idpBean, NodeList nodeList, int i) {

        Node nNode = nodeList.item(i);

        System.out.println("readAndSetIDPBean");

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) nNode;

            idpBean.setIdentityProviderName(eElement.getElementsByTagName("IdentityProviderName").item(0).getTextContent());
            idpBean.setDisplayName(eElement.getElementsByTagName("DisplayName").item(0).getTextContent());
            idpBean.setIdentityProviderDescription(eElement.getElementsByTagName("IdentityProviderDescription").item(0).getTextContent());
            idpBean.setenable(Boolean.parseBoolean(eElement.getElementsByTagName("enable").item(0).getTextContent()));
            idpBean.setAlias(eElement.getElementsByTagName("Alias").item(0).getTextContent());

            //setting claims to bean begin
            NodeList claimList = eElement.getElementsByTagName("ClaimConfig");
            ClaimConfig claimConfig = new ClaimConfig();

            ArrayList<Claim> localClaimIDPList = new ArrayList<Claim>();

            for (int j = 0; j < claimList.getLength(); j++) {

                Node claimNode = claimList.item(j);
                if (claimNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element claimElement = (Element) claimNode;
                    NodeList localClaimDialectList = claimElement.getElementsByTagName("localClaimDialect");
                    Node localClaimDialectNode = localClaimDialectList.item(0);
                    if (localClaimDialectNode != null && localClaimDialectNode.getNodeType() == Node.ELEMENT_NODE) {
                        claimConfig.setLocalClaimDialect(Boolean.valueOf(localClaimDialectNode.getTextContent()));
                    }
                    NodeList userClaimURIList = claimElement.getElementsByTagName("userClaimURI");
                    Node userClaimURINode = userClaimURIList.item(0);
                    if (userClaimURINode != null && userClaimURINode.getNodeType() == Node.ELEMENT_NODE) {
                        claimConfig.setUserClaimURI(userClaimURINode.getTextContent());
                    }

                    NodeList mappingList = claimElement.getElementsByTagName("claimMappings");
                    for (int k = 0; k < mappingList.getLength(); k++) {
                        Node mappingNode = mappingList.item(k);
                        if (mappingNode.getNodeType() == Node.ELEMENT_NODE) {
                            ClaimMapping mapping = new ClaimMapping();
                            Element mappingElement = (Element) mappingNode;
                            NodeList localClaimList = mappingElement.getElementsByTagName("localClaim");
                            for (int l = 0; l < localClaimList.getLength(); l++) {
                                Node localClaimNode = localClaimList.item(l);
                                if (localClaimNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element localClaimElement = (Element) localClaimNode;
                                    Claim localClaim = new Claim();
                                    NodeList claimUriList = localClaimElement.getElementsByTagName("claimUri");
                                    for (int m = 0; m < claimUriList.getLength(); m++) {
                                        Node claimUriNode = claimUriList.item(m);
                                        if (claimUriNode.getNodeType() == Node.ELEMENT_NODE) {
                                            localClaim.setClaimUri(claimUriNode.getTextContent());
                                            mapping.setLocalClaim(localClaim);

                                        }
                                    }

                                }
                            }

                            NodeList remoteClaimList = mappingElement.getElementsByTagName("remoteClaim");
                            for (int l = 0; l < remoteClaimList.getLength(); l++) {
                                Node localClaimNode = remoteClaimList.item(l);
                                if (localClaimNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element localClaimElement = (Element) localClaimNode;
                                    Claim remoteClaim = new Claim();
                                    NodeList claimUriList = localClaimElement.getElementsByTagName("claimUri");
                                    for (int m = 0; m < claimUriList.getLength(); m++) {
                                        Node claimUriNode = claimUriList.item(m);
                                        if (claimUriNode.getNodeType() == Node.ELEMENT_NODE) {
                                            remoteClaim.setClaimUri(claimUriNode.getTextContent());
                                            mapping.setRemoteClaim(remoteClaim);
                                            localClaimIDPList.add(remoteClaim);
                                        }
                                    }

                                }
                            }
                            claimConfig.addClaimMappings(mapping);
                        }
                    }
                }

            }

            claimConfig.setIdpClaims(localClaimIDPList.toArray(new Claim[localClaimIDPList.size()]));
            idpBean.setClaimConfig(claimConfig);
            //setting claims to bean ends


            //setting federated authenticator configs to bean begins
            NodeList federatedAuthenticatorConfigs = eElement.getElementsByTagName("FederatedAuthenticatorConfigs");
            for (int j = 0; j < federatedAuthenticatorConfigs.getLength(); j++) {
                FederatedAuthenticatorConfig saml2SSOAuthnConfig = new FederatedAuthenticatorConfig();
                Node node = federatedAuthenticatorConfigs.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList saml2Configs = element.getElementsByTagName("saml2");
                    for (int k = 0; k < saml2Configs.getLength(); k++) {
                        Node saml2Node = saml2Configs.item(k);
                        if (saml2Node.getNodeType() == Node.ELEMENT_NODE) {
                            Element saml2Element = (Element) saml2Node;
                            saml2SSOAuthnConfig.setName(saml2Element.getElementsByTagName("Name").item(0).getTextContent());
                            saml2SSOAuthnConfig.setDisplayName(saml2Element.getElementsByTagName("DisplayName").item(0).getTextContent());
                            saml2SSOAuthnConfig.setEnabled(Boolean.valueOf(saml2Element.getElementsByTagName("IsEnabled").item(0).getTextContent()));
                            NodeList propertyList = saml2Element.getElementsByTagName("property");
                            Property[] properties = new Property[12];
                            for (int l = 0; l < propertyList.getLength(); l++) {
                                Node propertyNode = propertyList.item(l);
                                if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element propertyElement = (Element) propertyNode;
                                    if ("IdpEntityId".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IDP_ENTITY_ID);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[0] = property;
                                    } else if ("IsLogoutEnabled".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_LOGOUT_ENABLED);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[1] = property;
                                    } else if ("SPEntityId".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.SP_ENTITY_ID);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[2] = property;
                                    } else if ("SSOUrl".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.SSO_URL);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[3] = property;
                                    } else if ("isAssertionSigned".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_ENABLE_ASSERTION_SIGNING);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[4] = property;
                                    } else if ("commonAuthQueryParams".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName("commonAuthQueryParams");
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[5] = property;
                                    } else if ("IsUserIdInClaims".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_USER_ID_IN_CLAIMS);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[6] = property;
                                    } else if ("IsLogoutReqSigned".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_LOGOUT_REQ_SIGNED);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[7] = property;
                                    } else if ("IsAssertionEncrypted".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_ENABLE_ASSERTION_ENCRYPTION);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[8] = property;
                                    } else if ("IsAuthReqSigned".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_AUTHN_REQ_SIGNED);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[9] = property;
                                    } else if ("IsAuthnRespSigned".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.IS_AUTHN_RESP_SIGNED);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[10] = property;
                                    } else if ("LogoutReqUrl".equals(propertyElement.getElementsByTagName("Name").item(0).getTextContent())) {
                                        Property property = new Property();
                                        property.setName(IdentityApplicationConstants.Authenticator.SAML2SSO.LOGOUT_REQ_URL);
                                        property.setValue(propertyElement.getElementsByTagName("Value").item(0).getTextContent());
                                        properties[11] = property;
                                    }
                                }
                            }
                            saml2SSOAuthnConfig.setProperties(properties);

                        }
                    }

                }
                idpBean.setFederatedAuthenticatorConfig(saml2SSOAuthnConfig);
            }
            //setting federated authenticator configs to bean ends

            //setting certificate begins here
            if (!eElement.getElementsByTagName("Certificate").item(0).getTextContent().isEmpty()) {
                File file = new File(certFilePath);
                byte[] bFile = new byte[(int) file.length()];
                FileInputStream fileInputStream = null;
                try {
                    //convert file into array of bytes
                    fileInputStream = new FileInputStream(file);
                    fileInputStream.read(bFile);
                    fileInputStream.close();
                } catch (Exception e) {
                    System.out.println("Error occured while reading the file");
                }
                //idpBean.setCertificate(Base64.encodeBase64String(bFile));
            }
            //setting certificate ends here

            //setting JustInTime Provisioning Configuration begins
            NodeList justInTimeProvisioningConfigList = eElement.getElementsByTagName("JustInTimeProvisioningConfig");
            JustInTimeProvisioningConfig justInTimeProvisioningConfig = new JustInTimeProvisioningConfig();
            for (int j = 0; j < justInTimeProvisioningConfigList.getLength(); j++) {
                Node justInTimeProvisioningConfigNode = justInTimeProvisioningConfigList.item(j);
                if (justInTimeProvisioningConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element justInTimeProvConfigElem = (Element) justInTimeProvisioningConfigNode;
                    if (justInTimeProvConfigElem.getElementsByTagName("provisioningEnabled").item(0) != null) {
                        boolean provEnabled = Boolean.valueOf(justInTimeProvConfigElem.getElementsByTagName("provisioningEnabled").item(0).getTextContent());
                        String provUserStore = justInTimeProvConfigElem.getElementsByTagName("provisioningUserStore").item(0).getTextContent();
                        String userStoreClaimUri = justInTimeProvConfigElem.getElementsByTagName("userStoreClaimUri").item(0).getTextContent();

                        justInTimeProvisioningConfig.setProvisioningEnabled(provEnabled);
                        justInTimeProvisioningConfig.setProvisioningUserStore(provUserStore);
                        justInTimeProvisioningConfig.setUserStoreClaimUri(userStoreClaimUri);

                    }

                }
            }

            idpBean.setJustInTimeProvisioningConfig(justInTimeProvisioningConfig);
            //setting JustInTime Provisioning Configuration ends

            //setting provisioning connector configs begins
            NodeList provisioningList = eElement.getElementsByTagName("ProvisioningConnectorConfigs");
            ProvisioningConnectorBean provisioningConnectorBean = new ProvisioningConnectorBean();
            ProvisioningConnectorConfig provisioningConnectorConfig = new ProvisioningConnectorConfig();
            for (int j = 0; j < provisioningList.getLength(); j++) {

                Node provisioningNode = provisioningList.item(j);
                if (provisioningNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element provisioningElement = (Element) provisioningNode;

                    NodeList provisioningConnectorList = provisioningElement.getElementsByTagName("ProvisioningConnectorConfig");
                    for (int k = 0; k < provisioningConnectorList.getLength(); k++) {

                        Node provisioningConnectorNode = provisioningConnectorList.item(k);
                        if (provisioningConnectorNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element provisioningConnectorElement = (Element) provisioningConnectorNode;
                            NodeList blockingList = provisioningConnectorElement.getElementsByTagName("blocking");
                            NodeList enabledList = provisioningConnectorElement.getElementsByTagName("enabled");
                            NodeList nameList = provisioningConnectorElement.getElementsByTagName("name");
                            if (blockingList.item(0) != null && "blocking".equals(blockingList.item(0).getNodeName())) {
                                provisioningConnectorBean.setBlocking(Boolean.valueOf(blockingList.item(0).getTextContent()));
                            }
                            if (enabledList.item(0) != null && "enabled".equals(enabledList.item(0).getNodeName())) {
                                provisioningConnectorBean.setEnabled(Boolean.valueOf(enabledList.item(0).getTextContent()));
                            }
                            if (nameList.item(0) != null && "name".equals(nameList.item(0).getNodeName())) {
                                provisioningConnectorBean.setName(nameList.item(0).getTextContent());
                            }


                            NodeList provisioningConnectorPropertiesList = provisioningConnectorElement.getElementsByTagName("ProvisioningProperties");
                            Node provisioningPropertiesNode = provisioningConnectorPropertiesList.item(0);
                            NodeList provisioningConnectorPropertyList = null;
                            if (provisioningPropertiesNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element provisioningConnectorPropertiesElement = (Element) provisioningPropertiesNode;

                                provisioningConnectorPropertyList = provisioningConnectorPropertiesElement.getElementsByTagName("Property");
                            }

                            Property[] provConnectorPropertyList = new Property[provisioningConnectorPropertyList.getLength()];


                            for (int l = 0; l < provisioningConnectorPropertyList.getLength(); l++) {
                                Node provisioningConnectorPropertyNode = provisioningConnectorPropertyList.item(l);
                                if (provisioningConnectorPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element provisioningConnectorPropertyElement = (Element) provisioningConnectorPropertyNode;

                                    NodeList namesList = provisioningConnectorPropertyElement.getElementsByTagName("name");


                                    NodeList valueList = provisioningConnectorPropertyElement.getElementsByTagName("value");

                                    if (StringUtils.isNotBlank(valueList.item(0).getTextContent())) {
                                        Property provisioningConnectorProperty = new Property();
                                        provisioningConnectorProperty.setName(namesList.item(0).getTextContent());
                                        provisioningConnectorProperty.setValue(valueList.item(0).getTextContent());
                                        provConnectorPropertyList[l] = provisioningConnectorProperty;
                                    }

                                }
                            }
                            provisioningConnectorBean.setProperties(provConnectorPropertyList);

                        }

                    }

                }
            }
            provisioningConnectorConfig.setBlocking(provisioningConnectorBean.isBlocking());
            provisioningConnectorConfig.setEnabled(provisioningConnectorBean.isEnabled());
            provisioningConnectorConfig.setName(provisioningConnectorBean.getName());
            provisioningConnectorConfig.setProvisioningProperties(provisioningConnectorBean.getProperties());

            idpBean.setProvisioningConnectorConfig(provisioningConnectorConfig);
            //setting provisioning connector configs ends


            //set role mappings **********

            NodeList permissionAndRoleMappingList = eElement.getElementsByTagName("PermissionsAndRoleConfig");
            RoleMappingBean roleMappingBean = new RoleMappingBean();
            for (int j = 0; j < permissionAndRoleMappingList.getLength(); j++) {

                Node roleMappingsNode = permissionAndRoleMappingList.item(j);

                if (roleMappingsNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element roleMappingElement = (Element) roleMappingsNode;

                    NodeList RoleMappingsList = roleMappingElement.getElementsByTagName("RoleMappings");

                    for (int k = 0; k < RoleMappingsList.getLength(); k++) {
                        Node RoleMappingNode = RoleMappingsList.item(k);

                        NodeList roleMappingPropertyList = null;
                        if (RoleMappingNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element roleMappingsElement = (Element) roleMappingsNode;
                            roleMappingPropertyList = roleMappingsElement.getElementsByTagName("RoleMapping");

                        }
                        Property[] roleMappingList = new Property[roleMappingPropertyList.getLength()];


                        for (int l = 0; l < roleMappingPropertyList.getLength(); l++) {
                            Node roleMappingPropertyNode = roleMappingPropertyList.item(l);
                            if (roleMappingPropertyNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element roleMappingPropertyElement = (Element) roleMappingPropertyNode;

                                NodeList localRoleList = roleMappingPropertyElement.getElementsByTagName("localRole");


                                NodeList remoteRoleList = roleMappingPropertyElement.getElementsByTagName("remoteRole");


                                Property roleMappingProperty = new Property();
                                roleMappingProperty.setName(localRoleList.item(0).getTextContent());
                                roleMappingProperty.setValue(remoteRoleList.item(0).getTextContent());
                                roleMappingList[l] = roleMappingProperty;


                            }
                        }
                        PermissionsAndRoleConfig permissionsAndRoleConfig = new PermissionsAndRoleConfig();
                        roleMappingBean.setProperties(roleMappingList);
                        RoleMapping[] mappings = new RoleMapping[roleMappingList.length];
                        for(int t=0; t<roleMappingList.length; t++){

                            Property property = roleMappingList[t];
                            RoleMapping mapping = new RoleMapping();
                            LocalRole local = new LocalRole();
                            local.setLocalRoleName(property.getName());
                            mapping.setLocalRole(local);
                            mapping.setRemoteRole(property.getValue());
                            permissionsAndRoleConfig.addIdpRoles(property.getValue());


                            permissionsAndRoleConfig.addRoleMappings(mapping);


                        }

                        idpBean.setPermissionAndRoleConfig(permissionsAndRoleConfig);
                    }

                }

            }

        }

    }

    /**
     * Fill the SPBean by reading the XML document
     *
     * @param spBean
     */
    public void readAndSetSPBean(SPBean spBean, NodeList nodeList, int i) {

        Node nNode = nodeList.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) nNode;

            spBean.setApplicationName(eElement.getElementsByTagName("ApplicationName").item(0).getTextContent());
            spBean.setDescription(eElement.getElementsByTagName("Description").item(0).getTextContent());

            NodeList claimList = eElement.getElementsByTagName("ClaimConfig");

            org.wso2.carbon.identity.application.common.model.xsd.ClaimConfig claimConfig = new org.wso2.carbon.identity.application.common.model.xsd.ClaimConfig();

            ArrayList<org.wso2.carbon.identity.application.common.model.xsd.Claim> localClaimIDPList = new ArrayList<org.wso2.carbon.identity.application.common.model.xsd.Claim>();

            for (int j = 0; j < claimList.getLength(); j++) {

                Node claimNode = claimList.item(j);
                if (claimNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element claimElement = (Element) claimNode;
                    NodeList localClaimDialectList = claimElement.getElementsByTagName("localClaimDialect");
                    Node localClaimDialectNode = localClaimDialectList.item(0);
                    if (localClaimDialectNode != null && localClaimDialectNode.getNodeType() == Node.ELEMENT_NODE) {
                        claimConfig.setLocalClaimDialect(Boolean.valueOf(localClaimDialectNode.getTextContent()));
                    }

                    NodeList mappingList = claimElement.getElementsByTagName("claimMappings");
                    for (int k = 0; k < mappingList.getLength(); k++) {
                        Node mappingNode = mappingList.item(k);
                        if (mappingNode.getNodeType() == Node.ELEMENT_NODE) {
                            org.wso2.carbon.identity.application.common.model.xsd.ClaimMapping mapping = new org.wso2.carbon.identity.application.common.model.xsd.ClaimMapping();
                            Element mappingElement = (Element) mappingNode;
                            NodeList requestedNodeList = mappingElement.getElementsByTagName("requested");
                            Node RequestedNode = requestedNodeList.item(0);
                            if (RequestedNode != null && RequestedNode.getNodeType() == Node.ELEMENT_NODE) {
                                mapping.setRequested(Boolean.valueOf(RequestedNode.getTextContent()));
                            }
                            NodeList localClaimList = mappingElement.getElementsByTagName("localClaim");
                            for (int l = 0; l < localClaimList.getLength(); l++) {
                                Node localClaimNode = localClaimList.item(l);
                                if (localClaimNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element localClaimElement = (Element) localClaimNode;
                                    org.wso2.carbon.identity.application.common.model.xsd.Claim localClaim = new org.wso2.carbon.identity.application.common.model.xsd.Claim();
                                    NodeList claimUriList = localClaimElement.getElementsByTagName("claimUri");
                                    for (int m = 0; m < claimUriList.getLength(); m++) {
                                        Node claimUriNode = claimUriList.item(m);
                                        if (claimUriNode.getNodeType() == Node.ELEMENT_NODE) {
                                            localClaim.setClaimUri(claimUriNode.getTextContent());
                                            mapping.setLocalClaim(localClaim);

                                        }
                                    }

                                }
                            }

                            NodeList remoteClaimList = mappingElement.getElementsByTagName("remoteClaim");
                            for (int l = 0; l < remoteClaimList.getLength(); l++) {
                                Node localClaimNode = remoteClaimList.item(l);
                                if (localClaimNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element localClaimElement = (Element) localClaimNode;
                                    org.wso2.carbon.identity.application.common.model.xsd.Claim remoteClaim = new org.wso2.carbon.identity.application.common.model.xsd.Claim();
                                    NodeList claimUriList = localClaimElement.getElementsByTagName("claimUri");
                                    for (int m = 0; m < claimUriList.getLength(); m++) {
                                        Node claimUriNode = claimUriList.item(m);
                                        if (claimUriNode.getNodeType() == Node.ELEMENT_NODE) {
                                            remoteClaim.setClaimUri(claimUriNode.getTextContent());
                                            mapping.setRemoteClaim(remoteClaim);
                                            localClaimIDPList.add(remoteClaim);
                                        }
                                    }

                                }
                            }
                            claimConfig.addClaimMappings(mapping);
                        }
                    }
                }

            }

            claimConfig.setIdpClaims(localClaimIDPList.toArray(new org.wso2.carbon.identity.application.common.model.xsd.Claim[localClaimIDPList.size()]));
            spBean.setClaimConfig(claimConfig);

            //setting authSteps begins
            NodeList localAndOutBoundAuthenticationConfigList = eElement.getElementsByTagName("LocalAndOutBoundAuthenticationConfig");
            LocalAndOutBoundAuthConfigBean localAndOutBoundAuthConfig = new LocalAndOutBoundAuthConfigBean();
            for (int j = 0; j < localAndOutBoundAuthenticationConfigList.getLength(); j++) {
                Node localAndOutBoundAuthenticationConfigNode = localAndOutBoundAuthenticationConfigList.item(j);
                if (localAndOutBoundAuthenticationConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element localAndOutBoundAuthenticationConfigElement = (Element) localAndOutBoundAuthenticationConfigNode;
                    NodeList authenticationStepList = localAndOutBoundAuthenticationConfigElement.getElementsByTagName("AuthenticationStep");
                    ArrayList<AuthenticationStepBean> authenticationSteps = new ArrayList<AuthenticationStepBean>();
                    for (int k = 0; k < authenticationStepList.getLength(); k++) {
                        Node authenticationStepNode = authenticationStepList.item(k);
                        if (authenticationStepNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element authenticationStepElement = (Element) authenticationStepNode;
                            int stepOrder = Integer.parseInt(authenticationStepElement.getElementsByTagName("StepOrder").item(0).getTextContent());
                            boolean subjectStep = Boolean.valueOf(authenticationStepElement.getElementsByTagName("SubjectStep").item(0).getTextContent());
                            boolean attributeStep = Boolean.valueOf(authenticationStepElement.getElementsByTagName("AttributeStep").item(0).getTextContent());
                            AuthenticationStepBean authenticationStepBean = new AuthenticationStepBean(stepOrder);
                            authenticationStepBean.setSubjectStep(subjectStep);
                            authenticationStepBean.setAttributeStep(attributeStep);

                            //setting local authenticators
                            NodeList localAuthenticatorList = authenticationStepElement.getElementsByTagName("LocalAuthenticatorConfig");
                            ArrayList<LocalAuthenticatorConfigBean> localAuthenticatorConfigList = new ArrayList<LocalAuthenticatorConfigBean>();
                            for (int l = 0; l < localAuthenticatorList.getLength(); l++) {
                                Node localAuthenticatorNode = localAuthenticatorList.item(l);
                                if (localAuthenticatorNode.getNodeType() == Node.ELEMENT_NODE) {
                                    LocalAuthenticatorConfigBean localAuthenticatorConfig = new LocalAuthenticatorConfigBean();
                                    Element localAuthenticatorElement = (Element) localAuthenticatorNode;
                                    String name = localAuthenticatorElement.getElementsByTagName("Name").item(0).getTextContent();
                                    String displayName = localAuthenticatorElement.getElementsByTagName("DisplayName").item(0).getTextContent();
                                    String isEnabled = localAuthenticatorElement.getElementsByTagName("IsEnabled").item(0).getTextContent();
                                    localAuthenticatorConfig.setName(name);
                                    localAuthenticatorConfig.setDisplayName(displayName);
                                    localAuthenticatorConfig.setEnabled(Boolean.valueOf(isEnabled));
                                    localAuthenticatorConfigList.add(localAuthenticatorConfig);

                                }
                            }
                            authenticationStepBean.setLocalAuthenticatorConfigList(localAuthenticatorConfigList);

                            //setting federated authenticators
                            NodeList federatedAuthenticatorList = authenticationStepElement.getElementsByTagName("FederatedIdentityProviders");
                            ArrayList<FederatedAuthenticatorConfigBean> federatedAuthenticatorConfigList = new ArrayList<FederatedAuthenticatorConfigBean>();
                            for (int l = 0; l < federatedAuthenticatorList.getLength(); l++) {
                                Node federatedAuthenticatorNode = federatedAuthenticatorList.item(l);
                                if (federatedAuthenticatorNode.getNodeType() == Node.ELEMENT_NODE) {
                                    FederatedAuthenticatorConfigBean federatedAuthenticatorConfig = new FederatedAuthenticatorConfigBean();
                                    Element federatedAuthenticatorElement = (Element) federatedAuthenticatorNode;
                                    if (federatedAuthenticatorElement.getElementsByTagName("IdentityProviderName").item(0) != null) {
                                        String idpName = federatedAuthenticatorElement.getElementsByTagName("IdentityProviderName").item(0).getTextContent();
                                        String isEnabled = federatedAuthenticatorElement.getElementsByTagName("IsEnabled").item(0).getTextContent();
                                        federatedAuthenticatorConfig.setIdpName(idpName);
                                        federatedAuthenticatorConfig.setEnabled(Boolean.valueOf(isEnabled));
                                        federatedAuthenticatorConfigList.add(federatedAuthenticatorConfig);
                                    }
                                }
                            }
                            authenticationStepBean.setFederatedAuthenticatorConfigBean(federatedAuthenticatorConfigList);
                            authenticationSteps.add(authenticationStepBean);
                        }
                    }
                    localAndOutBoundAuthConfig.setAuthSteps(authenticationSteps);
                }
            }
            spBean.setLocalAndOutBoundAuthConfigBean(localAndOutBoundAuthConfig);
            //setting authSteps ends


            //setting inbound authentication configuration begins
            NodeList inboundAuthenticationConfigList = eElement.getElementsByTagName("InboundAuthenticationConfig");
            for (int j = 0; j < inboundAuthenticationConfigList.getLength(); j++) {
                Node inboundAuthenticationConfigNode = inboundAuthenticationConfigList.item(j);
                if (inboundAuthenticationConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element inboundAuthenticationConfigNodeElement = (Element) inboundAuthenticationConfigNode;
                    NodeList inboundAuthReqConfigList = inboundAuthenticationConfigNodeElement.getElementsByTagName("InboundAuthenticationRequestConfig");
                    List<InboundAuthenticationRequestConfig> inboundAuthReqConfList = new ArrayList<InboundAuthenticationRequestConfig>();
                    for (int k = 0; k < inboundAuthReqConfigList.getLength(); k++) {
                        Node inboundAuthReqConfigNode = inboundAuthReqConfigList.item(k);
                        if (inboundAuthReqConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element inboundAuthReqConfigElement = (Element) inboundAuthReqConfigNode;
                            InboundAuthenticationRequestConfig inboundAuthRequestConfig = new InboundAuthenticationRequestConfig();
                            inboundAuthRequestConfig.setInboundAuthType(inboundAuthReqConfigElement.getElementsByTagName("InboundAuthType").item(0).getTextContent());

                            NodeList propertyList = inboundAuthReqConfigElement.getElementsByTagName("Properties");
                            List<org.wso2.carbon.identity.application.common.model.xsd.Property> propList = new ArrayList<org.wso2.carbon.identity.application.common.model.xsd.Property>();
                            for (int l = 0; l < propertyList.getLength(); l++) {
                                Node propertyNode = propertyList.item(l);
                                if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element propertyElement = (Element) propertyNode;
                                    org.wso2.carbon.identity.application.common.model.xsd.Property property = new org.wso2.carbon.identity.application.common.model.xsd.Property();
                                    property.setName(propertyElement.getElementsByTagName("Name").item(0).getTextContent());
                                    propList.add(property);
                                }
                                inboundAuthRequestConfig.setProperties(propList.toArray(new org.wso2.carbon.identity.application.common.model.xsd.Property[propList.size()]));
                            }
                            inboundAuthReqConfList.add(inboundAuthRequestConfig);
                        }

                    }

                    spBean.setInboundAuthenticationRequestConfigs(inboundAuthReqConfList);

                    //setting oauth App config
                    NodeList oauthAppList = inboundAuthenticationConfigNodeElement.getElementsByTagName("OAuthAPP");
                    for (int k = 0; k < oauthAppList.getLength(); k++) {
                        OAuthConsumerAppDTO oAuthConsumerAppDTO = new OAuthConsumerAppDTO();
                        Node oauthAppNode = oauthAppList.item(k);
                        if (oauthAppNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element oauthAppElement = (Element) oauthAppNode;
                            String version = oauthAppElement.getElementsByTagName("Version").item(0).getTextContent();
                            String callBackUrl = oauthAppElement.getElementsByTagName("CallBackURL").item(0).getTextContent();
                            String grantTypes = oauthAppElement.getElementsByTagName("GrantTypes").item(0).getTextContent();
                            String userName = oauthAppElement.getElementsByTagName("UserName").item(0).getTextContent();

                            oAuthConsumerAppDTO.setOAuthVersion(version);
                            oAuthConsumerAppDTO.setCallbackUrl(callBackUrl);
                            oAuthConsumerAppDTO.setGrantTypes(grantTypes);
                            oAuthConsumerAppDTO.setUsername(userName);
                            oAuthConsumerAppDTO.setApplicationName(spBean.getApplicationName());
                        }
                        spBean.setoAuthConsumerAppDTO(oAuthConsumerAppDTO);
                    }
                    //setting inbound authentication configuration ends

                    //setting IdentitySAMLSSOConfigService configuration begins
                    NodeList samlSSOConfigList = inboundAuthenticationConfigNodeElement.getElementsByTagName("IdentitySAMLSSOConfigService");
                    for (int k = 0; k < samlSSOConfigList.getLength(); k++) {
                        SAMLSSOServiceProviderDTO samlssoServiceProviderDTO = new SAMLSSOServiceProviderDTO();
                        Node samlSSOConfigNode = samlSSOConfigList.item(k);
                        if (samlSSOConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element samlSSOConfigElement = (Element) samlSSOConfigNode;
                            String issuer = samlSSOConfigElement.getElementsByTagName("Issuer").item(0).getTextContent();
                            String assertionConsumerURL = samlSSOConfigElement.getElementsByTagName("AssertionConsumerURL").item(0).getTextContent();
                            String nameIdFormat = samlSSOConfigElement.getElementsByTagName("NameIdFormat").item(0).getTextContent();
                            String enableResponseSigning = samlSSOConfigElement.getElementsByTagName("EnableResponseSigning").item(0).getTextContent();
                            String enableAssertionSigning = samlSSOConfigElement.getElementsByTagName("EnableAssertionSigning").item(0).getTextContent();
                            String includeAttributesInResponseAlways = samlSSOConfigElement.getElementsByTagName("IncludeAttributesInResponseAlways").item(0).getTextContent();

                            samlssoServiceProviderDTO.setIssuer(issuer);
                            samlssoServiceProviderDTO.setAssertionConsumerUrl(assertionConsumerURL);
                            samlssoServiceProviderDTO.setNameIDFormat(nameIdFormat);
                            samlssoServiceProviderDTO.setDoSignResponse(Boolean.valueOf(enableResponseSigning));
                            samlssoServiceProviderDTO.setDoSignAssertions(Boolean.valueOf(enableAssertionSigning));
                            samlssoServiceProviderDTO.setEnableAttributesByDefault(Boolean.valueOf(includeAttributesInResponseAlways));

                            NodeList requestedAudienceList = samlSSOConfigElement.getElementsByTagName("Audience");
                            ArrayList<String> requestedAudiencesList = new ArrayList<String>();
                            for (int l = 0; l < requestedAudienceList.getLength(); l++) {
                                Node requestedAudienceNode = requestedAudienceList.item(l);
                                if (requestedAudienceNode.getNodeType() == Node.ELEMENT_NODE) {
                                    requestedAudiencesList.add(requestedAudienceNode.getTextContent());
                                }
                            }
                            samlssoServiceProviderDTO.setRequestedAudiences(requestedAudiencesList.toArray(new String[requestedAudiencesList.size()]));
                        }
                        spBean.setSamlssoServiceProviderDTO(samlssoServiceProviderDTO);
                    }

                    //setting IdentitySAMLSSOConfigService configuration ends
                }
            }


            //setting outbound provisioning configuration begins
            NodeList outBoundProvisioningConfigList = eElement.getElementsByTagName("OutboundProvisioningConfig");
            for (int j = 0; j < outBoundProvisioningConfigList.getLength(); j++) {
                Node outBoundProvisioningConfigNode = outBoundProvisioningConfigList.item(j);
                if (outBoundProvisioningConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element outBoundProvisioningConfigElement = (Element) outBoundProvisioningConfigNode;
                    NodeList idpNameList = outBoundProvisioningConfigElement.getElementsByTagName("IdentityProviderName");
                    List<String> idpNamesList = new ArrayList<String>();
                    for (int k = 0; k < idpNameList.getLength(); k++) {
                        Node idpNameNode = idpNameList.item(k);
                        if (idpNameNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element idpNameElement = (Element) idpNameNode;
                            idpNamesList.add(idpNameElement.getTextContent());
                        }
                    }
                    spBean.setIdpNameList(idpNamesList);
                }
            }


            NodeList permissionAndRoleMappingList = eElement.getElementsByTagName("PermissionsAndRoleConfig");
            RoleMappingBean roleMappingBean = new RoleMappingBean();
            PermissionsAndRoleConfig permissionsAndRoleConfig = new PermissionsAndRoleConfig();
            for (int j = 0; j < permissionAndRoleMappingList.getLength(); j++) {

                Node roleMappingsNode = permissionAndRoleMappingList.item(j);

                if (roleMappingsNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element roleMappingElement = (Element) roleMappingsNode;

                    NodeList RoleMappingsList = roleMappingElement.getElementsByTagName("RoleMappings");

                    for (int k = 0; k < RoleMappingsList.getLength(); k++) {
                        Node RoleMappingNode = RoleMappingsList.item(k);

                        NodeList roleMappingPropertyList = null;
                        if (RoleMappingNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element roleMappingsElement = (Element) roleMappingsNode;
                            roleMappingPropertyList = roleMappingsElement.getElementsByTagName("RoleMapping");

                        }
                        Property[] roleMappingList = new Property[roleMappingPropertyList.getLength()];


                        for (int l = 0; l < roleMappingPropertyList.getLength(); l++) {
                            Node roleMappingPropertyNode = roleMappingPropertyList.item(l);
                            if (roleMappingPropertyNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element roleMappingPropertyElement = (Element) roleMappingPropertyNode;

                                NodeList localRoleList = roleMappingPropertyElement.getElementsByTagName("localRole");


                                NodeList remoteRoleList = roleMappingPropertyElement.getElementsByTagName("remoteRole");


                                Property roleMappingProperty = new Property();
                                roleMappingProperty.setName(localRoleList.item(0).getTextContent());
                                roleMappingProperty.setValue(remoteRoleList.item(0).getTextContent());
                                roleMappingList[l] = roleMappingProperty;


                            }
                        }
                        roleMappingBean.setProperties(roleMappingList);

                    }

                }

            }

        }



    }


}

