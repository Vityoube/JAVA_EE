package org.vkalashnykov.api;

import com.caucho.burlap.client.BurlapProxyFactory;
import com.caucho.burlap.client.BurlapProxyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.vkalashnykov.service.UserService;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vkalashnykov on 03.01.17.
 */
public class BurlapAPI {
    private static BurlapProxyFactory proxyFactory;
    @Autowired
    private  static UserService userService;

    public static BurlapProxyFactory burlapChatService() throws IOException {
        proxyFactory=new BurlapProxyFactory();
        userService=(UserService)proxyFactory.create(UserService.class,"http://localhost:8090/BurlapService");
        return proxyFactory;
    }

    public static BurlapProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public static void setProxyFactory(BurlapProxyFactory proxyFactory) {
        BurlapAPI.proxyFactory = proxyFactory;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static void configureBurlap() throws IOException {
        burlapChatService();
    }
}
