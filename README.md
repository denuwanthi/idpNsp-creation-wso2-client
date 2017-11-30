# idpNsp-creation-wso2-client
A SOAP client to create an IDP and SP

1.Edit identity-provider.xml file and provide relevent parameters with in xml tags for,

I.SCIM configuraion. Please refer doccumentation [1] for more information. 
II.Role mapping configuraion. Please refer doccumentation [2] for more information.

3.Save the file.

[1] https://docs.wso2.com/display/IS530/Outbound+Provisioning+with+SCIM

[2] https://docs.wso2.com/display/IS530/Configuring+Roles+for+an+Identity+Provider


3.Run the idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar using following command by providing relevent inputs as arguments.
------------------------------------------------------
Add IDP
-------------------------------------------------------
java -jar  idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar $ADMIN_USER $ADMIN_PASSWORD $IS_HOST_NAME https://$IS_HOST_NAME:$IS_HTTPS_PORT/services/ $FILE_LOCATION_IDP_CONFIGUARAION_FILE  $JKS_FILE_LOCATION  $NAME_IDP_CONFIGUARAION_FILE IDP $JKS_FILE_PASSWORD Add

1.$ADMIN_USER - username to login to carbon console (ex -admin)

2.$ADMIN_PASSWORD - password to login to carbon console (ex -admin)

3.$IS_HOST_NAME - hostaname of the IS node.(ex -localhost)

4.$IS_HTTPS_PORT -Port of the IS node which run.(ex -9443)

5.$FILE_LOCATION_IDP_CONFIGUARAION_FILE - Path to identity-provider.xml file.

6.$JKS_FILE_LOCATION - Path to .jks file (ex -  wso2is-5.3.0/repository/resources/security/client-truststore.jks ).

7.$NAME_IDP_CONFIGUARAION_FILE - Name of the IDP configuration file (ex- identity-provider.xml).

8.IDP - fixed parameter, Since we need to add IDP

9.Add - fixed parameter, Since we call addIDP method

As an example,Could you please refer below command

java -jar  idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar admin admin localhost https://localhost:9443/services/ /home/siluni/issues/TRAVISPERKINSDEV-252/svnrepo/sp_idp/target/identity-provider.xml  /home/siluni/issues/TRAVISPERKINSDEV-252/isnode1/wso2is-5.3.0/repository/resources/security/client-truststore.jks  identity-provider.xml IDP wso2carbon Add

------------------------------------------------------
Update IDP
-------------------------------------------------------


java -jar  idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar $ADMIN_USER $ADMIN_PASSWORD $ADMIN_PASSWORD $IS_HOST_NAME https://$IS_HOST_NAME:$IS_HTTPS_PORT/services/ $FILE_LOCATION_IDP_CONFIGUARAION_FILE  $JKS_FILE_LOCATION  $NAME_IDP_CONFIGUARAION_FILE IDP $JKS_FILE_PASSWORD Update $NAME_OF_THE_IDP
1.$ADMIN_USER - username to login to carbon console (ex -admin)

2.$ADMIN_PASSWORD - password to login to carbon console (ex -admin)

3.$IS_HOST_NAME - hostaname of the IS node.(ex -localhost)

4.$IS_HTTPS_PORT -Port of the IS node which run.(ex -9443)

5.$FILE_LOCATION_IDP_CONFIGUARAION_FILE - Path to identity-provider.xml file.

6.$JKS_FILE_LOCATION - Path to .jks file (ex -  wso2is-5.3.0/repository/resources/security/client-truststore.jks ).

7.$NAME_IDP_CONFIGUARAION_FILE - Name of the IDP configuration file (ex- identity-provider.xml).

8.IDP - fixed parameter, Since we need to add IDP

9.Add - fixed parameter, Since we call addIDP method

10.$NAME_OF_THE_IDP - Name of the IDP that you are going to update.

As an example,Could you please refer below command

java -jar  idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar admin admin localhost https://localhost:9443/services/ /home/siluni/issues/TRAVISPERKINSDEV-252/svnrepo/sp_idp/target/identity-provider.xml  /home/siluni/issues/TRAVISPERKINSDEV-252/isnode1/wso2is-5.3.0/repository/resources/security/client-truststore.jks  identity-provider.xml IDP wso2carbon Update WSO2IDP


*You can use the similar way to create a SP. You need to have a service-provider.xml to create a SP


*In order to debug you can use a cpmmand "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"  infront of -jar similar to this


*java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005  idpAndSPAutomate-1.0-SNAPSHOT-jar-with-dependencies.jar admin admin localhost https://localhost:9443/services/ /home/wso2dinali/Documents/IdpClient_version2_project/sp_idp/identity-provider.xml  /home/wso2dinali/SUPPORT/travispakin/isnode1/wso2is-5.3.0/repository/resources/security/client-truststore.jks identity-provider.xml IDP wso2carbon Add






