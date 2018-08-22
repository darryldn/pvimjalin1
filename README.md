# pvimjalin1  
pvim jalin  
  
Required for compile and running PVIM_DNI_Ext  
google gson-2.3.1  
  
apache dbutils  
commons-dbutils-1.7  
  
Google api client for java  
https://developers.google.com/api-client-library/java/google-api-java-client/download  
  
Firebase geofire API client for java  
https://github.com/firebase/geofire-java  
geofire-common-2.3.1.jar  
geofire-java-2.3.0.jar  
  
Spring 4.3.9 all  
  
Apache commons logging version 1.1.1  
  
firebase admin:  
firebase-admin-6.2.0.jar  
  
google commons:  
api-common-1.6.0.jar  
  
google auth library  
google-auth-library-credentials-0.9.1.jar  
google-auth-library-oauth2-http-0.9.1.jar  
  
google guava  
guava-25.1-jre.jar  
  
netty  
netty-all-4.1.25.Final.jar  
  
slfj4 1.7.25  
  
  
For PvimExtTelegramNotification  

TL;DR
Just grab all libraries from pvim.ear  
  
For more detailed:  
jboss-javaee.jar (only for compiling dependency), can be obtained from jboss version 5.1.0 GA  
mail-1.4.jar, available from pvim.ear  
rab-server.jar, available from pvim.ear  
spring*.jar from pvim.ear  
apache dbutils 1.7  
hibernate*.jar from pvim.ear  
gson-2.3.1.jar  
  
  
IDE  
Netbeans 8.2  
  
Output:  
PVIM_DNI_Ext is an EAR library  
put PVIM_DNI_Ext.ear in standalone directory in wildfly, the same folder for pvim.ear  
  
PvimExtTelegramNotification is a jar library  
put PvimExtTelegramNotification.jar, FirebaseCommons.jar, PvimCommons.jar and TelegramCommons.jar in pvim.ear lib folder  
  
  
For GpsCsv2PvDBImporter  
commons-dbutils-1.7.jar  
jdts-1.2.8.jar  
log4j-1.2.15.jar  
commons-csv-1.5.jar  
JDK 8  


PVIM_DMZ  
PvimDmz.war  
requires: https://github.com/mitre/HTTP-Proxy-Servlet  
Simple one java file servlet proxy  
  