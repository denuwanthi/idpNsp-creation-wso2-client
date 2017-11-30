package org.wso2.carbon;

import org.wso2.carbon.bean.IDPBean;
import org.wso2.carbon.identity.application.common.model.idp.xsd.FederatedAuthenticatorConfig;
import org.wso2.carbon.identity.application.common.model.idp.xsd.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.idp.xsd.ProvisioningConnectorConfig;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceIdentityProviderManagementExceptionException;
import org.wso2.carbon.idp.mgt.stub.IdentityProviderMgtServiceStub;
import org.wso2.carbon.util.AuthenticateStubUtil;
import java.util.logging.*;
import java.rmi.RemoteException;
import java.util.List;

public class UpdateIDPClient {

    private static Logger logger = Logger.getLogger("org.wso2.carbon");

    private IdentityProviderMgtServiceStub idpMgtStub;
    private final String serviceName = "IdentityProviderMgtService";

    public UpdateIDPClient(String backendUrl, String sessionCookie) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpMgtStub = new IdentityProviderMgtServiceStub(endPoint);


        AuthenticateStubUtil.authenticateStub(sessionCookie, idpMgtStub);
    }

    public UpdateIDPClient(String backendUrl, String userName, String password) throws Exception {
        String endPoint = backendUrl + serviceName;
        idpMgtStub = new IdentityProviderMgtServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, idpMgtStub);
    }

    /**
     * Fill the identity provider object from idp bean and call the addIdp method to add the identity provider
     *
     * @param idpBeansList
     * @throws RemoteException
     */
    public void UpdateIDPs(List<IDPBean> idpBeansList,String idpName)
            throws RemoteException, IdentityProviderMgtServiceIdentityProviderManagementExceptionException {
        for (IDPBean idpBean : idpBeansList) {


            IdentityProvider identityProviderUpdate = new IdentityProvider();
            identityProviderUpdate.setIdentityProviderName(idpBean.getIdentityProviderName());
            identityProviderUpdate.setDisplayName(idpBean.getDisplayName());
            identityProviderUpdate.setIdentityProviderDescription(idpBean.getIdentityProviderDescription());
            identityProviderUpdate.setEnable(idpBean.getenable());
            identityProviderUpdate.setAlias(idpBean.getAlias());
            identityProviderUpdate.setCertificate(idpBean.getCertificate());


            //setting claims here
            logger.log(Level.INFO,"Setting claim configs for idp begins......");
           // identityProvider.setClaimConfig(idpBean.getClaimConfig());
            identityProviderUpdate.setClaimConfig(idpBean.getClaimConfig());
            logger.log(Level.INFO,"Setting claim configs for idp ends......");


            //saml2ssoConfiguration
            logger.log(Level.INFO,"Setting SAML2 SSO configuration for idp begins.......");
            //identityProvider.setDefaultAuthenticatorConfig(idpBean.getFederatedAuthenticatorConfig());
            identityProviderUpdate.setDefaultAuthenticatorConfig(idpBean.getFederatedAuthenticatorConfig());
          //  identityProvider.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{idpBean.getFederatedAuthenticatorConfig()});
            identityProviderUpdate.setFederatedAuthenticatorConfigs(new FederatedAuthenticatorConfig[]{idpBean.getFederatedAuthenticatorConfig()});
            logger.log(Level.INFO,"Setting SAML2 SSO configuration for idp ends.......");

            //setting justInTime provisioning
            logger.log(Level.INFO,"Setting just in time provisioning configs for idp begins......");
           // identityProvider.setJustInTimeProvisioningConfig(idpBean.getJustInTimeProvisioningConfig());
            identityProviderUpdate.setJustInTimeProvisioningConfig(idpBean.getJustInTimeProvisioningConfig());
            logger.log(Level.INFO,"Setting just in time provisioning configs for idp ends......");

            //adding provisioning connector configs
            logger.log(Level.INFO,"Setting provisioning connector configs for idp begins...........");
            if (idpBean.getProvisioningConnectorConfig().getName() != null) {
             //   identityProvider.setProvisioningConnectorConfigs(new ProvisioningConnectorConfig[]{idpBean.getProvisioningConnectorConfig()});
                identityProviderUpdate.setProvisioningConnectorConfigs(new ProvisioningConnectorConfig[]{idpBean.getProvisioningConnectorConfig()});
            }

            logger.log(Level.INFO,"setting Role based configs...........");
            if (idpBean.getPermissionAndRoleConfig() != null) {
                identityProviderUpdate.setPermissionAndRoleConfig(idpBean.getPermissionAndRoleConfig());

            }
            logger.log(Level.INFO,"Setting provisioning connector configs for idp ends...........");

            logger.log(Level.INFO,"Calling web service to update identity providers begins........");

            //update role mapping in IDP.
            idpMgtStub.updateIdP(idpName, identityProviderUpdate);


            logger.log(Level.INFO,"Calling web service to update identity providers ends........");
        }

    }
}
