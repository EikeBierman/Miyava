package com.miyava.serie.controller;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miyava.common.AbstractController;
import com.miyava.serie.model.Serie;
import com.miyava.serie.service.SerieDao;
import com.miyava.util.AjaxUtils;
import com.miyava.util.BreadCrumbs;
import com.miyava.util.MessageHelper;

@Controller
@RequestMapping( SerieController.BASE_URL )
public class SerieController
    extends AbstractController {

    protected final static String BASE_URL = "/serie";

    protected final static String CREATE_FORM = "serie/createForm";
    
    private static final String SERIE_VIEW_NAME = "serie/editForm";

    private static final String SERIE_TAB_VIEW_NAME = "serie/serietabs";

    @Autowired
    private SerieDao serieDao;

    public SerieController() {
        super( BASE_URL );
    }

    @RequestMapping( method = RequestMethod.GET )
    public String dashboard( Model model ) {
        BreadCrumbs.set( model, "serie.page.list.breadcrumb" );
        return "serie/serie";
    }

    @RequestMapping( "/new" )
    public String createForm( Model model ) {
        BreadCrumbs.set( model, "serie", "serie.page.list.breadcrumb", "serie.page.create.breadcrumb" );
        model.addAttribute( "serie", new Serie() );
        return CREATE_FORM;
    }

    @RequestMapping( value = "/create", method = RequestMethod.POST )
    public String createAction( @Valid Serie serie, Errors errors, RedirectAttributes ra, Model model ) {
        if ( errors.hasErrors() ) {
            return CREATE_FORM;
        }

        Serie savedSerie = serieDao.doSave( serie, errors );
        if ( savedSerie != null ) {
            MessageHelper.addSuccessAttribute( ra, "common.message.success_create" );
            return "redirect:/serie/" + savedSerie.getId();
        }
        else {
            MessageHelper.addGlobalCreateErrorAttribute( model, errors );
            return CREATE_FORM;
        }
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String show( @PathVariable Long id, Model model ) { 
        model.addAttribute( "serie", serieDao.findOne( id ) );
        return SERIE_VIEW_NAME;
    }
    
    @RequestMapping( value = "/{id}/maintab", method = RequestMethod.GET )
    public String mainTab( @PathVariable Long id, Model model,
                           @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {

       model.addAttribute( "serie", serieDao.findOne( id ) );

        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return SERIE_TAB_VIEW_NAME.concat( " :: maintab" );
        }
        else {
            return SERIE_VIEW_NAME;
        }
    }
    
    @RequestMapping( value = "/{id}/listtab", method = RequestMethod.GET )
    public String listSeries( @PathVariable Long id, Model model,
                              @RequestHeader( value = "X-Requested-With", required = false )String requestedWith ) {
        model.addAttribute( "lists",getList() );
        
        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return SERIE_TAB_VIEW_NAME.concat( " :: listtab" );
        }
        else {
            return SERIE_VIEW_NAME;
        }
    }
    
    @RequestMapping( value = "/{id}/update", method = RequestMethod.POST, headers = "X-Requested-With=XMLHttpRequest" )
    public String editAction( @PathVariable Long id, @Valid Serie serie, Errors errors, RedirectAttributes ra, Model model ) {

        if ( !errors.hasErrors() ) {
            Serie savedSerie = serieDao.doUpdate( serie, errors );
            if ( savedSerie == null ) {
                MessageHelper.addGlobalUpdateErrorAttribute( model, errors );
            }
            else {
                model.addAttribute( "serie", savedSerie );
                MessageHelper.addSuccessAttribute( model, "common.message.success_update" );
            }
        }
        return SERIE_TAB_VIEW_NAME.concat( " :: maintab" );
    }
    
    private List<String> getList(){
        URL url = null;
        try {
            url = new URL("http://www.fernsehserien.de/the-walking-dead/episodenguide");
        } catch ( MalformedURLException e1 ) {
            e1.printStackTrace();
        }
        
        List<String> list = new ArrayList<String>();
        String patternAll = "<td(.*?)itemprop=\"episodeNumber\">(.*?)</td><td(.*?)><span(.*?)>(.*?)</span></td><td(.*?)><span(.*?)>(.*?)</span>(.*?)<span itemprop=\"name\">(.*?)</span>(.*?)<td(.*?)class=\"episodenliste-ea\"(.*?)>(.*?)<span class=\"episodenliste-oea-smartphone\">(.*?)</span></td>";      
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            while (reader.readLine() != null) {
                for (String line; (line = reader.readLine()) != null;) {
                    Pattern pattern = Pattern.compile(patternAll);
                    Matcher matcher = pattern.matcher(line);
                    while(matcher.find()){
                        list.add( matcher.group( 2 ) + " " + matcher.group( 5 ) + " " + matcher.group( 8 ) + " " + matcher.group( 10 ) + " " + matcher.group( 14 ) ); // Gibt alle Deutschen Informationen aus
                    }
               }
            }
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return list;

    }
}
