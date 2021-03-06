package org.vkalashnykov.configuration;

import org.vkalashnykov.api.BurlapAPI;
import org.vkalashnykov.api.HessianAPI;
import org.vkalashnykov.api.XmlRpcAPI;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public class FrameworkConfiguration {
    public enum Frameworks{
        XMLRPC("xmlrpc"),HESSIAN("hessian"),BURLAP("burlap");

        private String implementation;

        Frameworks(String implemenation){
            this.implementation=implemenation;
        }

        public String getImplementation() {
            return implementation;
        }
    }
    private static String frameworkImplementation=Frameworks.XMLRPC.getImplementation();

    public static String getFrameworkImplementation() {
        return frameworkImplementation;
    }

    public static void setFrameworkImplementation(String frameworkImplementation) {
        FrameworkConfiguration.frameworkImplementation = frameworkImplementation;
    }

    public static void configureFrameworks() throws IOException {
        XmlRpcAPI.configureXmlRpcClient();
        BurlapAPI.configureBurlap();
        HessianAPI.configureHessian();
    }
}
