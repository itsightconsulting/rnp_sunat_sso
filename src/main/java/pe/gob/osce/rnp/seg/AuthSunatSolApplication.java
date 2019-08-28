package pe.gob.osce.rnp.seg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AuthSunatSolApplication extends SpringBootServletInitializer {


    @Autowired
    public ApplicationContext ctx;
    public static void main(String[] args) {
        SpringApplication.run(AuthSunatSolApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AuthSunatSolApplication.class);
    }
}

