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


import java.util.ArrayList;

/**
 * Bean class to holds authentication steps
 */
public class AuthenticationStepBean {

    private ArrayList<LocalAuthenticatorConfigBean> localAuthenticatorConfigList;
    private ArrayList<FederatedAuthenticatorConfigBean> federatedAuthenticatorConfigBean;
    private int stepOrder;
    private boolean subjectStep;
    private boolean attributeStep;

    public AuthenticationStepBean(int stepOrder){
        this.stepOrder = stepOrder;
    }

    public ArrayList<LocalAuthenticatorConfigBean> getLocalAuthenticatorConfigList() {
        return localAuthenticatorConfigList;
    }

    public void setLocalAuthenticatorConfigList(ArrayList<LocalAuthenticatorConfigBean> localAuthenticatorConfigList) {
        this.localAuthenticatorConfigList = localAuthenticatorConfigList;
    }

    public ArrayList<FederatedAuthenticatorConfigBean> getFederatedAuthenticatorConfigBean() {
        return federatedAuthenticatorConfigBean;
    }

    public void setFederatedAuthenticatorConfigBean(ArrayList<FederatedAuthenticatorConfigBean> federatedAuthenticatorConfigBean) {
        this.federatedAuthenticatorConfigBean = federatedAuthenticatorConfigBean;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public boolean isSubjectStep() {
        return subjectStep;
    }

    public void setSubjectStep(boolean subjectStep) {
        this.subjectStep = subjectStep;
    }

    public boolean isAttributeStep() {
        return attributeStep;
    }

    public void setAttributeStep(boolean attributeStep) {
        this.attributeStep = attributeStep;
    }
}
