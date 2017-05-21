package com.miyava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SuppressWarnings( "deprecation" )
@SpringBootApplication
@EnableJpaRepositories( repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class )
public class MiyavaApplication
    extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(
                                                  SpringApplicationBuilder application ) {
        return application.sources( MiyavaApplication.class );
    }

    public static void main( String[] args ) {
        SpringApplication.run( MiyavaApplication.class, args );
    }
}
