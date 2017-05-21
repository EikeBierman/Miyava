package com.miyava.util;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageUtils {

    @Resource
    private MessageSource messageSource;

    /**
     * Return a localized message from the resource bundle. If no message for the given key could be found, return the
     * key itself as the result.
     *
     * @param key
     * @param params
     * @return
     * @see #translate(Locale, String, Object...)
     */
    public String translate( String key, Object... params ) {
        return translate( LocaleContextHolder.getLocale(), key, params );
    }

    /**
     * Return a localized message from the resource bundle. If no message for the given key could be found, return the
     * key itself as the result.
     *
     * @param locale
     * @param key
     * @param params
     * @return
     */
    public String translate( Locale locale, String key, Object... params ) {

        String result = key; // default behaviour
        Object[] argsObj = {}; // message params
        if ( params != null ) {
            argsObj = new Object[params.length];
            for ( int i = 0; i < params.length; i++ ) {
                argsObj[i] = params[i];
            }
        }
        try {
            result = messageSource.getMessage( key, argsObj, locale );
        }
        catch ( NoSuchMessageException e ) {
            // ignore: it's sometimes intended to not find an appropriate translation
        }
        return result;
    }

}
