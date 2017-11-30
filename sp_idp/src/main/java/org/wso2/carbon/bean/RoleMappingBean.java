package org.wso2.carbon.bean;

import org.wso2.carbon.identity.application.common.model.idp.xsd.Property;

public class RoleMappingBean {

    private Property[] properties;


    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }
}
