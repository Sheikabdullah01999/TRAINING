package com.grootan.assetManagement.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.grootan.assetManagement.Model.Constants.WEB_URL;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI StudentServiceOpenAPI()
    {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title("Asset management")
                        .title("Asset management")
                        .version("0.1")
                        .description("Rest api to manage assets")
                        .license(new License().name("Licensed To Grootan Technology").url(WEB_URL)));
                        //.schemaRequirement("Authorization", new SecurityScheme().scheme("basic")
                      // .in(SecurityScheme.In.HEADER).type(SecurityScheme.Type.HTTP));

    }

}
