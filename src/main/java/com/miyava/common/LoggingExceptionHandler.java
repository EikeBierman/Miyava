package com.miyava.common;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;

@ControllerAdvice
public class LoggingExceptionHandler
    implements EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger( LoggingExceptionHandler.class );

    private Environment environment;

    @ExceptionHandler
    public ModelAndView logException( HttpServletRequest req, Throwable exception ) {
        LOGGER.error( String.format( "An error has occurred: %s", exception.getMessage() ), exception );

        ModelAndView mav = new ModelAndView();
        mav.addObject( "exception", exception );
        mav.addObject( "timestamp", new Date() );
        mav.addObject( "url", req.getRequestURL() );
        mav.addObject( "error", exception.getClass() );
        mav.addObject( "message", buildMessage( exception ) );
        mav.setViewName( "error" );
        return mav;
    }

    private String buildMessage( Throwable exception ) {
        String message = exception.getMessage();
        String stackTrace = ExceptionUtils.getStackTrace( exception );
        String appName = environment.getProperty( "spring.application.name" );

        String baseMessage = null;
        if ( Strings.isNullOrEmpty( appName ) ) {
            baseMessage = "Error in Application : ";
        }
        else {
            baseMessage = "Error in Application " + appName + " : ";
        }

        return baseMessage
            + String.valueOf( message )
            + "<div style=\"display:none\">\n"
            + message
            + "\n"
            + stackTrace
            + "</div>";
    }

    @Override
    public void setEnvironment( Environment environment ) {
        this.environment = environment;
    }
}
