package org.vkalashnykov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.vkalashnykov.service.UserService;
import org.vkalashnykov.service.UserServiceImpl;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.vkalashnykov.persistence")
public class ChatServerApplication {
    @Autowired
    private UserServiceImpl userServiceImpl;

	public static void main(String[] args) {
		SpringApplication.run(ChatServerApplication.class, args);
	}

	@Bean(name = "/UserService")
	public HessianServiceExporter hessianChatService(){
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(userServiceImpl);
        exporter.setServiceInterface(UserService.class);
        return exporter;
	}
}
