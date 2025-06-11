package net.adamgoodridge.sequencetrackplayer.config;

import nz.net.ultraq.thymeleaf.layoutdialect.*;
import org.springframework.boot.web.client.*;
import org.springframework.context.annotation.*;
import org.springframework.web.client.*;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring6.*;
import org.thymeleaf.spring6.templateresolver.*;
import org.thymeleaf.spring6.view.*;
import org.thymeleaf.templatemode.*;
import org.thymeleaf.templateresolver.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.adamgoodridge.sequencetrackplayer")
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }
   @Bean
   public ITemplateResolver thymeleafTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:views/");
        templateResolver.setSuffix(".html");
       templateResolver.setTemplateMode(TemplateMode.HTML);
       templateResolver.setCacheable(false);
       templateResolver.setCheckExistence(false);
        //templateResolver.setTemplateMode("HTML5")
        return templateResolver;
    }
    // (User-defined Property)
    //https://o7planning.org/11867/configure-spring-boot-to-redirect-http-to-https
    @Bean
    public ThymeleafViewResolver templateResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        return viewResolver;
    }
    //https://www.baeldung.com/spring-mvc-static-resources
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/content/**")
                .addResourceLocations("classpath:/static/");
    }
}
