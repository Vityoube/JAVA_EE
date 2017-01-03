package org.vkalashnykov;

import com.caucho.burlap.server.BurlapServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.web.HttpRequestHandler;
import org.vkalashnykov.controller.XmlRpcController;
import org.vkalashnykov.service.UserService;
import org.vkalashnykov.service.UserServiceImpl;

import javax.servlet.ServletException;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.vkalashnykov.persistence")
public class ChatServerApplication {
    @Autowired
    private XmlRpcController xmlRpcController;
    @Autowired
    private UserServiceImpl userServiceImpl;


	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

	@Bean(name = "/HessianService")
	public HessianServiceExporter hessianChatService(){
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(userServiceImpl);
        exporter.setServiceInterface(UserService.class);
        return exporter;
	}

	@Bean(name="/BurlapService")
	public BurlapServiceExporter burlapChatService() throws ServletException {
        BurlapServiceExporter burlapService=new BurlapServiceExporter();
        burlapService.setService(userServiceImpl);
        burlapService.setServiceInterface(UserService.class);
		return burlapService;
	}


}
