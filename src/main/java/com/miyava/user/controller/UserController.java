package com.miyava.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.miyava.common.AbstractController;
import com.miyava.user.dto.PasswordForm;
import com.miyava.user.model.User;
import com.miyava.user.service.UserDao;
import com.miyava.util.AjaxUtils;
import com.miyava.util.BreadCrumbs;
import com.miyava.util.MessageHelper;
import com.miyava.util.ValidationResponse;

@Controller
@RequestMapping( UserController.BASE_URL )
public class UserController
    extends AbstractController {

    protected final static String BASE_URL = "/user";

    private static final String PROFILE_VIEW_NAME = "user/editForm";

    private static final String PROFILE_TAB_VIEW_NAME = "user/usertabs";

    @Value( "${user.password_minlength}" )
    private Integer passwordMinlength;

    @Autowired
    private UserDao userDao;

    public UserController() {
        super( BASE_URL );
    }

    @ModelAttribute
    public void populatePasswordMinlength( Model model ) {
        model.addAttribute( "passwordMinlength", passwordMinlength );
    }

    @RequestMapping( method = RequestMethod.GET )
    public String dashboard( Model model ) {
        BreadCrumbs.set( model, "user.page.list.breadcrumb" );
        return "user/user";
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String show( @PathVariable String id, Model model ) {
        BreadCrumbs.set( model, "user", "user.page.list.breadcrumb", "user.page.edit.breadcrumb" );

        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            MessageHelper.addErrorAttribute( model, "user.messages.not_allowed_modify_profile" );
        }

        model.addAttribute( "user", userDao.findOne( id ) );
        return PROFILE_VIEW_NAME;
    }

    @RequestMapping( value = "/{id}/passwordtab", method = RequestMethod.GET )
    public String passwordTab( @PathVariable String id, Model model,
                               @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {

        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            MessageHelper.addErrorAttribute( model, "user.messages.not_allowed_modify_profile" );
        }

        model.addAttribute( "user", userDao.findOne( id ) );
        model.addAttribute( "passwordForm", new PasswordForm() );

        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return PROFILE_TAB_VIEW_NAME.concat( " :: passwordtab" );
        }
        else {
            return PROFILE_VIEW_NAME;
        }
    }

    @RequestMapping( value = "/{id}/maintab", method = RequestMethod.GET )
    public String mainTab( @PathVariable String id, Model model,
                           @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {

        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            MessageHelper.addErrorAttribute( model, "user.messages.not_allowed_modify_profile" );
        }

        model.addAttribute( "user", userDao.findOne( id ) );

        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return PROFILE_TAB_VIEW_NAME.concat( " :: maintab" );
        }
        else {
            return PROFILE_VIEW_NAME;
        }
    }

    @RequestMapping( "/new" )
    public String createForm( Model model ) {
        BreadCrumbs.set( model, "user", "user.page.list.breadcrumb", "user.page.create.breadcrumb" );
        model.addAttribute( "user", new User() );
        return "user/createForm";
    }

    @RequestMapping( value = "/create", method = RequestMethod.POST )
    public String createAction( @Valid User user, Errors errors, RedirectAttributes ra, Model model ) {
        if ( errors.hasErrors() ) {
            return "user/createForm";
        }

        User savedUser = userDao.doSave( user, errors );
        if ( savedUser != null ) {
            MessageHelper.addSuccessAttribute( ra, "common.message.success_create" );
            return "redirect:/user/" + savedUser.getId();
        }
        else {
            MessageHelper.addGlobalCreateErrorAttribute( model, errors );
            return "user/createForm";
        }
    }

    @RequestMapping( value = "/{id}/update", method = RequestMethod.POST, headers = "X-Requested-With=XMLHttpRequest" )
    public String editAction( @PathVariable String id, @Valid User user, Errors errors, RedirectAttributes ra, Model model ) {

        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            MessageHelper.addErrorAttribute( model, "user.messages.not_allowed_modify_profile" );
        }
        else if ( !errors.hasErrors() ) {
            User savedUser = userDao.doUpdate( user, errors );
            if ( savedUser == null ) {
                MessageHelper.addGlobalUpdateErrorAttribute( model, errors );
            }
            else {
                model.addAttribute( "user", savedUser );
                MessageHelper.addSuccessAttribute( model, "common.message.success_update" );
            }
        }

        return PROFILE_TAB_VIEW_NAME.concat( " :: maintab" );
    }

    @RequestMapping( value = "/{id}/updatepassword", method = RequestMethod.POST, headers = "X-Requested-With=XMLHttpRequest" )
    public @ResponseBody ValidationResponse updatePasswordAction( @PathVariable String id, @Valid PasswordForm passwordForm, Errors errors,
                                                                  RedirectAttributes ra, Model model ) {
        if ( Strings.isNullOrEmpty( passwordForm.getCurrentPassword() ) ) {
            errors.rejectValue( "currentPassword", "user.messages.password_empty" );
        }
        if ( Strings.isNullOrEmpty( passwordForm.getPasswordConfirm() ) ) {
            errors.rejectValue( "passwordConfirm", "user.messages.password_confirmation_empty" );
        }
        if ( errors.hasErrors() ) {
            return extractAjaxFieldErrors( errors );
        }

        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            return extractAjaxError( "user.messages.not_allowed_modify_profile" );
        }

        User currentUser = userDao.findOne( id );
        if ( currentUser == null ) {
            return extractAjaxError( "common.message.id_not_exists", new Object[] { id } );
        }

        currentUser.setPassword( passwordForm.getCurrentPassword() );

        User savedUser = userDao.doUpdate( currentUser, errors );
        if ( savedUser != null ) {
            return extractAjaxSuccess( "common.message.success_update" );
        }
        else {
            return extractAjaxGlobalUpdateError( errors );
        }
    }

    @RequestMapping( value = "/{id}/delete", method = RequestMethod.GET )
    public String deleteAction( @PathVariable String id, RedirectAttributes ra, Model model, User user, Errors errors ) {
        // User darf sein eigenes Profil nicht bearbeiten
        if ( isCurrentUser( id ) ) {
            errors.reject( "user.messages.not_allowed_modify_profile" );
        }

        if ( errors.hasErrors() ) {
            return "user/editForm";
        }

        if ( userDao.doDelete( id, errors ) ) {
            MessageHelper.addSuccessAttribute( ra, "common.message.success_delete" );
            return "redirect:/user";
        }
        else {
            MessageHelper.addGlobalDeleteErrorAttribute( model, errors );
            return "user/list";
        }
    }

    private User getUser() {
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ( principal instanceof UserDetails ) {
            username = ( (UserDetails) principal ).getUsername();
        }
        else {
            username = principal.toString();
        }

        return userDao.findOneByUsername( username );
    }

    private boolean isCurrentUser( String id ) {
        User currentUser = getUser();
        if ( currentUser != null && currentUser.getId().equals( id ) ) {
            return true;
        }

        return false;
    }
}
