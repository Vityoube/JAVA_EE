package org.vkalashnykov.controller;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.vkalashnykov.service.UserService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class XmlRpcController {
    private XmlRpcServletServer server;
    @Autowired
    UserService userService;

    @PostConstruct
    public void init() throws XmlRpcException {
        try {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            System.out.println("Starting the server on port 8090");
            XmlRpcServerConfigImpl config= new XmlRpcServerConfigImpl();
            config.setEnabledForExtensions(true);

            server=new XmlRpcServletServer();
            server.setConfig(config);

            PropertyHandlerMapping xmlRpcHandlerMapping=new PropertyHandlerMapping();
            xmlRpcHandlerMapping.setRequestProcessorFactoryFactory(pClass->pRequest->userService);
            xmlRpcHandlerMapping.addHandler(UserService.class.getSimpleName(),UserService.class);
            XmlRpcSystemImpl.addSystemHandler(xmlRpcHandlerMapping);
            server.setHandlerMapping(xmlRpcHandlerMapping);
            System.out.println("The server started successfully.");

        } catch (Exception e) {
            System.err.println("Server Error: " + e);
            throw new XmlRpcException(e.getMessage());
        }
    }

    @RequestMapping("xmlrpc")
    public  void serve(HttpServletRequest request, HttpServletResponse response) throws XmlRpcException {
        try {
            server.execute(request,response);
        } catch (Exception e) {
            throw new XmlRpcException(e.getMessage(), e);
        }
    }


}
