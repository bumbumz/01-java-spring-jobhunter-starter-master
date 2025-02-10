package vn.pcv.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//StaticResourcesWebConfiguration.java
@Configuration
public class StaticResourcesWebConfiguration
        implements WebMvcConfigurer {

    @Value("${pcv.upload-file.base-uri}")
    private String baseUri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(baseUri);
    }
}
