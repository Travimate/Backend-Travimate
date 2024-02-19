package id.synrgy.travimate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

  @Value("${openapi.dev-url}")
  private String devUrl;

  @Value("${openapi.prod-url}")
  private String prodUrl;

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl(devUrl);
    devServer.setDescription("Server URL in Development environment");

    Server prodServer = new Server();
    prodServer.setUrl(prodUrl);
    prodServer.setDescription("Server URL in Production environment");

    Contact contact = new Contact();
    contact.setEmail("mail@travimate.com");
    contact.setName("TRAVIMATE BACKEND");
    contact.setUrl("https://travimate.backend");

    License mitLicense = new License().name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("TRAVIMATE BACKEND")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage backend of travimate.")
            .termsOfService("https://www.travimate.com/terms")
        .license(mitLicense)
        .version("3.1.0");

    return new OpenAPI().info(info).setOpenapi("3.0.0").servers(List.of(devServer, prodServer));
  }
}
