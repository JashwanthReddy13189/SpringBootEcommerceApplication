package com.ecommerce.project.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String MJ_Ecommerce_API_VERSION = "1.0";
    private static final String MJ_Ecommerce_TITLE = " MJ Ecommerce API";
    private static final String MJ_Ecommerce_DESC = " MJ Ecommerce Application";
    private static final String DEFAULT_CONTACT_NAME = " mr.jashwanthreddy";
    private static final String DEFAULT_CONTACT_EMAIL = " mr.jashwanthreddy@gmail.com";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(MJ_Ecommerce_TITLE)
                        .description(MJ_Ecommerce_DESC)
                        .version(MJ_Ecommerce_API_VERSION)
                        .contact(new Contact()
                                .name(DEFAULT_CONTACT_NAME)
                                .email(DEFAULT_CONTACT_EMAIL)));
    }

}
