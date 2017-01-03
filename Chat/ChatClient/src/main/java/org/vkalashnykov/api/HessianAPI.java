package org.vkalashnykov.api;


import com.caucho.hessian.client.HessianProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vkalashnykov.service.UserService;

import java.net.MalformedURLException;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public class HessianAPI {
    private static HessianProxyFactory proxyFactory;

    @Autowired
    private  static UserService userService;


    public static HessianProxyFactory hessianChatService() throws MalformedURLException {
        proxyFactory =new HessianProxyFactory();
        proxyFactory.setHessian2Reply(true);
        proxyFactory.setHessian2Request(true);
        userService= (UserService) proxyFactory.create(UserService.class,
                "http://localhost:8090/HessianService");
            return  proxyFactory;
    }

    public static HessianProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public static void setProxyFactory(HessianProxyFactory proxyFactory) {
        HessianAPI.proxyFactory = proxyFactory;
    }

    public static void configureHessian() throws MalformedURLException {
        hessianChatService();
    }

    public static UserService getUserService() {
        return userService;
    }
}
