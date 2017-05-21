package com.miyava.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.miyava.user.service.UserDao;
import com.miyava.util.ErrorMessage;
import com.miyava.util.Message;
import com.miyava.util.MessageUtils;
import com.miyava.util.ValidationResponse;

public abstract class AbstractController {

    @Autowired
    protected MessageUtils messageUtils;

    protected String baseUrl;

    @Autowired
    private UserDao userDao;


    public AbstractController( String baseUrl ) {
        this.baseUrl = baseUrl;
    }

    /**
     * Sorgt dafür, dass leere Strings als null gemappt werden.
     *
     * @param binder
     */
    @InitBinder
    public void initBinder( WebDataBinder binder ) {
        binder.registerCustomEditor( String.class, new StringTrimmerEditor( true ) );
    }

    @ModelAttribute
    public void currentName ( Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute( "currentUser", userDao.findOneByUsername( auth.getName() ) );
    }

    @ModelAttribute
    public void populateBaseUrl( Model model ) {
        model.addAttribute( "baseUrl", baseUrl );
    }

    // protected Map<String, Breadcrumb> getBasicBreadcrumb() {
    // Map<String, Breadcrumb> breadcrumb = Maps.newHashMap();
    // breadcrumb.put( "home", new Breadcrumb( "common.menu.dashboard", "/" ) );
    //
    // return breadcrumb;
    // }
    //
    // public void populateBreadcrumb( Model model, Map<String, Breadcrumb> breadcrumb ) {
    // model.addAttribute( "breadcrumb", breadcrumb );
    // }

    protected ValidationResponse extractAjaxFieldErrors( Errors errors ) {
        ValidationResponse res = new ValidationResponse();
        res.setStatus( ValidationResponse.Status.FAIL );
        List<FieldError> allErrors = errors.getFieldErrors();
        List<ErrorMessage> errorMesages = new ArrayList<ErrorMessage>();
        for ( FieldError objectError : allErrors ) {
            errorMesages
                .add( new ErrorMessage( objectError.getField(),
                    messageUtils.translate( objectError.getCode(), objectError.getArguments() ) ) );
        }
        res.setErrorMessageList( errorMesages );

        return res;
    }

    protected ValidationResponse extractAjaxSuccess( String message, Object... args ) {
        return extractAjaxMessage( message, Message.Type.SUCCESS, ValidationResponse.Status.SUCCESS, args );
    }

    protected ValidationResponse extractAjaxError( String message, Object... args ) {
        return extractAjaxMessage( message, Message.Type.ERROR, ValidationResponse.Status.FAIL, args );
    }

    protected ValidationResponse extractAjaxGlobalError( String message, Object... args ) {
        return extractAjaxMessage( message, Message.Type.ERROR, ValidationResponse.Status.FAIL, args );
    }

    protected ValidationResponse extractAjaxGlobalUpdateError( Errors errors ) {
        if ( errors.hasFieldErrors() ) {
            return extractAjaxFieldErrors( errors );
        }

        if ( errors.hasGlobalErrors() ) {
            return extractAjaxGlobalError( errors.getGlobalError().getCode() );
        }
        else {
            return extractAjaxGlobalError( "common.message.error_update" );
        }
    }

    protected ValidationResponse extractAjaxGlobalDeleteError( Errors errors ) {
        if ( errors.hasFieldErrors() ) {
            return extractAjaxFieldErrors( errors );
        }

        if ( errors.hasGlobalErrors() ) {
            return extractAjaxGlobalError( errors.getGlobalError().getCode() );
        }
        else {
            return extractAjaxGlobalError( "common.message.error_delete" );
        }
    }

    protected ValidationResponse extractAjaxMessage( String messageKey, Message.Type type, ValidationResponse.Status status,
                                                     Object... args ) {
        ValidationResponse res = new ValidationResponse();
        res.setStatus( status );
        res.setMessage( new Message( messageUtils.translate( messageKey, args ), type ) );

        return res;
    }
}
