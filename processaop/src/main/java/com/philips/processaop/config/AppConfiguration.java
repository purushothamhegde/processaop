package com.philips.processaop.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.siperian.sif.client.SiperianClient;

@Configuration
public class AppConfiguration {
    
    private static Logger log = LoggerFactory.getLogger(AppConfiguration.class);
    
    private static final String SIPERIAN_CLIENT_ORSID = "siperian-client.orsId";
    private static final String SIPERIAN_CLIENT_USERNAME = "siperian-client.username";
    private static final String SIPERIAN_CLIENT_PASSWORD = "siperian-client.password";
    private static final String SIPERIAN_CLIENT_PROTOCOL = "siperian-client.protocol";
    private static final String HTTP_CALL_URL = "http.call.url";
    private static final String SOAP_CALL_URL = "soap.call.url";
    private static final String JAVA_NAMING_PROVIDER_URL = "java.naming.provider.url";
    private static final String JAVA_NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
    private static final String JAVA_NAMING_FACTORY_URL_PKGS = "java.naming.factory.url.pkgs";
    private static final String JAVA_NAMING_SECURITY_PRINCIPAL = "java.naming.security.principal";
    private static final String JAVA_NAMING_SECURITY_CREDENTIALS = "java.naming.security.credentials";
    private static final String JBOSS_NAMING_CLIENT_EJB_CONTEXT = "jboss.naming.client.ejb.context";
    private static final String CORBA_LOCALHOST = "com.ibm.CORBA.LocalHost";
    
    @Autowired
    private Environment env;

    @Bean
    public SiperianClient siperianClient() {
        
        String siperianClientOrsId = env.getProperty(SIPERIAN_CLIENT_ORSID, String.class);
        String siperianClientUsername = env.getProperty(SIPERIAN_CLIENT_USERNAME, String.class);
        String siperianClientPassword = env.getProperty(SIPERIAN_CLIENT_PASSWORD, String.class);
        String siperianClientProtocol = env.getProperty(SIPERIAN_CLIENT_PROTOCOL, String.class);
        String httpCallURL = env.getProperty(HTTP_CALL_URL, String.class);
        String soapCallURL = env.getProperty(SOAP_CALL_URL, String.class);
        String javaNamingProviderURL = env.getProperty(JAVA_NAMING_PROVIDER_URL, String.class);
        String javaNamingFactoryInitial = env.getProperty(JAVA_NAMING_FACTORY_INITIAL, String.class);
        String javaNamingFactoryURLPkgs = env.getProperty(JAVA_NAMING_FACTORY_URL_PKGS, String.class);
        String javaNamingSecurityPrincipal = env.getProperty(JAVA_NAMING_SECURITY_PRINCIPAL, String.class);
        String javaNamingSecurityCredentials = env.getProperty(JAVA_NAMING_SECURITY_CREDENTIALS, String.class);
        String jbossNamingClientEJBContaxt = env.getProperty(JBOSS_NAMING_CLIENT_EJB_CONTEXT, String.class);
        String corbaLocalhost = env.getProperty(CORBA_LOCALHOST, String.class);

        Properties siperianClientProperties = new Properties();
        
        if (siperianClientOrsId != null) {
            siperianClientProperties.setProperty(SIPERIAN_CLIENT_ORSID, siperianClientOrsId);
        }
        
        if (siperianClientUsername != null) {
            siperianClientProperties.setProperty(SIPERIAN_CLIENT_USERNAME, siperianClientUsername);
        }
        
        if (siperianClientPassword != null) {
            siperianClientProperties.setProperty(SIPERIAN_CLIENT_PASSWORD, siperianClientPassword);
        }
        
        if (siperianClientProtocol != null) {
            siperianClientProperties.setProperty(SIPERIAN_CLIENT_PROTOCOL, siperianClientProtocol);
        }
        
        if (httpCallURL != null) {
            siperianClientProperties.setProperty(HTTP_CALL_URL, httpCallURL);
        }
        
        if (soapCallURL != null) {
            siperianClientProperties.setProperty(SOAP_CALL_URL, soapCallURL);
        }
        
        if (javaNamingProviderURL != null) {
            siperianClientProperties.setProperty(JAVA_NAMING_PROVIDER_URL, javaNamingProviderURL);
        }
        
        if (javaNamingFactoryInitial != null) {
            siperianClientProperties.setProperty(JAVA_NAMING_FACTORY_INITIAL, javaNamingFactoryInitial);
        }

        if (javaNamingFactoryURLPkgs != null) {
            siperianClientProperties.setProperty(JAVA_NAMING_FACTORY_URL_PKGS, javaNamingFactoryURLPkgs);
        }
        
        if (javaNamingSecurityPrincipal != null) {
            siperianClientProperties.setProperty(JAVA_NAMING_SECURITY_PRINCIPAL, javaNamingSecurityPrincipal);
        }
        
        if (javaNamingSecurityCredentials != null) {
            siperianClientProperties.setProperty(JAVA_NAMING_SECURITY_CREDENTIALS, javaNamingSecurityCredentials);
        }
        
        if (jbossNamingClientEJBContaxt != null) {
            siperianClientProperties.setProperty(JBOSS_NAMING_CLIENT_EJB_CONTEXT, jbossNamingClientEJBContaxt);
        }
        
        if (corbaLocalhost != null) {
            siperianClientProperties.setProperty(CORBA_LOCALHOST, corbaLocalhost);
        }
        
        return SiperianClient.newSiperianClient(siperianClientProperties);
    }
}
