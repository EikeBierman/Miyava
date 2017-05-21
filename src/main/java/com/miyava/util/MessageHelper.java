package com.miyava.util;

import static com.miyava.util.Message.MESSAGE_ATTRIBUTE;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class MessageHelper {

    private MessageHelper() {

    }

    public static void addSuccessAttribute( RedirectAttributes ra, String message, Object... args ) {
        addAttribute( ra, message, Message.Type.SUCCESS, args );
    }

    public static void addErrorAttribute( RedirectAttributes ra, String message, Object... args ) {
        addAttribute( ra, message, Message.Type.ERROR, args );
    }

    public static void addInfoAttribute( RedirectAttributes ra, String message, Object... args ) {
        addAttribute( ra, message, Message.Type.INFO, args );
    }

    public static void addWarningAttribute( RedirectAttributes ra, String message, Object... args ) {
        addAttribute( ra, message, Message.Type.WARNING, args );
    }

    private static void addAttribute( RedirectAttributes ra, String message, Message.Type type, Object... args ) {
        ra.addFlashAttribute( MESSAGE_ATTRIBUTE, new Message( message, type, args ) );
    }

    public static void addSuccessAttribute( Model model, String message, Object... args ) {
        addAttribute( model, message, Message.Type.SUCCESS, args );
    }

    public static void addErrorAttribute( Model model, String message, Object... args ) {
        addAttribute( model, message, Message.Type.ERROR, args );
    }

    public static void addInfoAttribute( Model model, String message, Object... args ) {
        addAttribute( model, message, Message.Type.INFO, args );
    }

    public static void addWarningAttribute( Model model, String message, Object... args ) {
        addAttribute( model, message, Message.Type.WARNING, args );
    }

    private static void addAttribute( Model model, String message, Message.Type type, Object... args ) {
        model.addAttribute( MESSAGE_ATTRIBUTE, new Message( message, type, args ) );
    }

    public static void addGlobalUpdateErrorAttribute( Model model, Errors errors ) {
        if ( errors.hasGlobalErrors() ) {
            addErrorAttribute( model, errors.getGlobalError().getCode() );
        }
        else {
            addErrorAttribute( model, "common.message.error_update" );
        }
    }

    public static void addGlobalDeleteErrorAttribute( Model model, Errors errors ) {
        if ( errors.hasGlobalErrors() ) {
            addErrorAttribute( model, errors.getGlobalError().getCode() );
        }
        else {
            addErrorAttribute( model, "common.message.error_delete" );
        }
    }

    public static void addGlobalCreateErrorAttribute( Model model, Errors errors ) {
        if ( errors.hasGlobalErrors() ) {
            addErrorAttribute( model, errors.getGlobalError().getCode() );
        }
        else {
            addErrorAttribute( model, "common.message.error_create" );
        }

    }
}
