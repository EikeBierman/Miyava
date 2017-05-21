package com.miyava.genres.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miyava.common.AbstractController;
import com.miyava.genres.model.Genres;
import com.miyava.genres.service.GenresDao;
import com.miyava.util.BreadCrumbs;
import com.miyava.util.MessageHelper;

@Controller
@RequestMapping( GenresController.BASE_URL )
public class GenresController
    extends AbstractController {

    public GenresController() {
        super( BASE_URL );
    }

    protected final static String BASE_URL = "/genres";

    protected final static String CREATE_FORM = "genres/createForm";
    protected final static String GENRE_VIEW_NAME = "genres/editForm";

    @Autowired
    private GenresDao genresDao;

    @RequestMapping( method = RequestMethod.GET )
    public String dashboard( Model model ) {
        BreadCrumbs.set( model, "genres.page.list.breadcrumb" );
        return "genres/genres";
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String show( @PathVariable Long id, Model model ) {
        BreadCrumbs.set( model, BASE_URL, "genres.page.list.breadcrumb", "genres.page.edit.breadcrumb" );
        model.addAttribute( "genres", genresDao.findOne( id ) );
        return GENRE_VIEW_NAME;
    }

    @RequestMapping( "/new" )
    public String createForm( Model model ) {
        BreadCrumbs.set( model, "genres", "genres.page.list.breadcrumb", "genres.page.create.breadcrumb" );
        model.addAttribute( "genres", new Genres() );
        return CREATE_FORM;
    }

    @RequestMapping( value = "/create", method = RequestMethod.POST )
    public String createAction( @Valid Genres genres, Errors errors, RedirectAttributes ra, Model model ) {
        if ( errors.hasErrors() ) {
            return CREATE_FORM;
        }

        Genres savedGenres = genresDao.doSave( genres, errors );
        if ( savedGenres != null ) {
            MessageHelper.addSuccessAttribute( ra, "common.message.success_create" );
            return "redirect:/genre/" + savedGenres.getId();
        }
        else {
            MessageHelper.addGlobalCreateErrorAttribute( model, errors );
            return CREATE_FORM;
        }
    }

}
