package pe.gob.osce.rnp.seg.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${caching}")
    private boolean caching;

    @Value("${spring.profiles.active}")
    private String profileActive;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(caching)
            registry.addResourceHandler("/img/**",
                    "/fonts/**",
                    "/css/**",
                    "/js/**")
                    .addResourceLocations(
                            "classpath:/static/img/",
                            "classpath:/static/fonts/",
                            "classpath:/static/css/",
                            "classpath:/static/js/")
                    .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
        else
            registry.addResourceHandler("/img/**",
                    "/fonts/**","/css/**")
                    .addResourceLocations(
                            "classpath:/static/img/",
                            "classpath:/static/fonts/",
                            "classpath:/static/css/")
                    .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)).resourceChain(true)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
}
