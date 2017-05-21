package com.miyava.configuration;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.filter.CharacterEncodingFilter;

@SuppressWarnings( "deprecation" )
@Configuration
public class WebConfiguration {

    /**
     * Charset Encoding
     */
    @Bean
    protected FilterRegistrationBean charsetEncodingFilter() {

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding( "UTF-8" );

        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter( characterEncodingFilter );

        return filterRegBean;
    }

    /**
     * Config für FileUpload
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // TODO: move this settings to config file
        factory.setMaxFileSize( "26214400" );
        factory.setMaxRequestSize( "26214400" );
        return factory.createMultipartConfig();
    }

    /**
     * @param conversionService
     * @return converter that maps between IDs and entities
     */
    @Bean
    public DomainClassConverter<?> domainClassConverter( FormattingConversionService conversionService ) {
        return new DomainClassConverter<FormattingConversionService>( conversionService );
    }
}
