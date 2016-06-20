package fi.vm.sade.oikeustulkkirekisteri.resource.config;

import com.fasterxml.classmate.GenericType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.alternates.Alternates;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletContext;
import java.util.concurrent.Callable;

/**
 * User: tommiratamaa
 * Date: 31.5.2016
 * Time: 12.35
 */
@Configuration
@EnableSwagger
@Profile("default")
public class SwaggerConfig {
    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin swaggerPlugin(ServletContext ctx) {
        RelativeSwaggerPathProvider relativeSwaggerPathProvider = new RelativeSwaggerPathProvider(ctx);
        relativeSwaggerPathProvider.setApiResourcePrefix("api");
        final TypeResolver typeResolver = new TypeResolver();
        @SuppressWarnings("unchecked")
        SwaggerSpringMvcPlugin plugin = new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .excludeAnnotations(InternalApi.class)
                .pathProvider(relativeSwaggerPathProvider)
                .genericModelSubstitutes(ResponseEntity.class, Optional.class)
                .alternateTypeRules(Alternates.newRule(typeResolver.resolve(new GenericType<Callable<ResponseEntity<Object>>>() {
                        }), typeResolver.resolve(Object.class)))
                .swaggerGroup("public");
        return plugin;
    }

    /**
     * API Info as it appears on the swagger-ui page
     */
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Oppijan verkkopalvelukokonaisuus / Oikeustulkkirekisterin rajapinta",
                "Spring MVC API based on the swagger 1.2 spec",
                "https://confluence.csc.fi/display/oppija/Rajapinnat+toisen+asteen+ja+perusasteen+toimijoille",
                null,
                "EUPL 1.1",
                "http://ec.europa.eu/idabc/eupl"
        );
        return apiInfo;
    }
}