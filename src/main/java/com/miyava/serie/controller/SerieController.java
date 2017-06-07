package com.miyava.serie.controller;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.*;
import com.miyava.common.AbstractController;
import com.miyava.genres.model.Genres;
import com.miyava.genres.service.GenresDao;
import com.miyava.serie.model.*;
import com.miyava.serie.service.SerieDao;
import com.miyava.util.*;

@Controller
@RequestMapping( SerieController.BASE_URL )
public class SerieController
    extends AbstractController {

    protected final static String BASE_URL = "/serie";

    protected final static String CREATE_FORM = "serie/createForm";

    private static final String SERIE_VIEW_NAME = "serie/editForm";

    private static final String SERIE_TAB_VIEW_NAME = "serie/serietabs";

    private static final String THEMOVIEDB_API_KEY = "e32412fcd041dff3927fd5c7c5498600";

    private static final String THEMOVIEDB_LANG = "de_DE";

    @Autowired
    private SerieDao serieDao;

    @Autowired
    private GenresDao genreDao;

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
                              @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {
        model.addAttribute( "lists", getList() );

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

    private List<String> getList() {
        URL url = null;
        try {
            url = new URL( "http://www.fernsehserien.de/the-walking-dead/episodenguide" );
        }
        catch ( MalformedURLException e1 ) {
            e1.printStackTrace();
        }

        List<String> list = new ArrayList<String>();
        String patternAll =
            "<td(.*?)itemprop=\"episodeNumber\">(.*?)</td><td(.*?)><span(.*?)>(.*?)</span></td><td(.*?)><span(.*?)>(.*?)</span>(.*?)<span itemprop=\"name\">(.*?)</span>(.*?)<td(.*?)class=\"episodenliste-ea\"(.*?)>(.*?)<span class=\"episodenliste-oea-smartphone\">(.*?)</span></td>";

        try (BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), "UTF-8" ) )) {
            while ( reader.readLine() != null ) {
                for ( String line; ( line = reader.readLine() ) != null; ) {
                    Pattern pattern = Pattern.compile( patternAll );
                    Matcher matcher = pattern.matcher( line );
                    while ( matcher.find() ) {
                        list.add( matcher.group( 2 ) + " " + matcher.group( 5 ) + " " + matcher.group( 8 ) + " " + matcher.group( 10 ) + " "
                            + matcher.group( 14 ) ); // Gibt alle Deutschen Informationen aus
                    }
                }
            }
        }
        catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        return list;

    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/add/{id}_old", method = RequestMethod.GET )
    public String addSeries_old( @PathVariable Long id, @Valid Serie serie, Errors errors,
                                 RedirectAttributes ra, Model model ) {
        URL url = null;
        URL urlSeason = null;
        URL urlEpisode = null;
        serie = null;
        int cut = 100;
        int a = 0;

        ObjectMapper jsonMapperSerie = new ObjectMapper();
        jsonMapperSerie.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        ObjectMapper jsonMapperSeason = new ObjectMapper();
        jsonMapperSeason.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        ObjectMapper jsonMapperEpisode = new ObjectMapper();
        jsonMapperEpisode.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        errors = null;
        boolean done = false;
        while ( !done ) {
            try {
                url = new URL(
                    "https://api.themoviedb.org/3/tv/" + id + "?api_key=" + THEMOVIEDB_API_KEY + "&language=" + THEMOVIEDB_LANG );
                BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), "UTF-8" ) );
                String jsonText = readAll( reader );
                serie = jsonMapperSerie.readValue( jsonText, Serie.class );
                if ( serie != null ) {
                    for ( Genres s : serie.getGenres() ) {
                        if ( genreDao.findOneByName( s.getName() ) != null ) {
                            s.setId(
                                genreDao.findOneByName( s.getName() ).getId() );
                            s.setCreatedBy( genreDao.findOneByName( s.getName() ).getCreatedBy() );
                            s.setCreatedDate( genreDao.findOneByName( s.getName() ).getCreatedDate() );
                            s.setLastModifiedBy( genreDao.findOneByName( s.getName() ).getLastModifiedBy() );
                            s.setLastModifiedDate(
                                genreDao.findOneByName( s.getName() ).getLastModifiedDate() );
                        }
                    }
                    if ( serie.getOverview() == null ||
                        serie.getOverview().isEmpty() ) {
                        serie.setOverview( "Zurzeit gibt es keine Beschreibung" );
                    }
                    if ( serie.getName() == null || serie.getName().isEmpty() ) {
                        serie.setName( "Zurzeit gibt es kein Title" );
                    }
                    if ( serie.getPoster_path() == null || serie.getPoster_path().isEmpty() ) {
                        serie.setPoster_path(
                            "http://manntheatres.com/images/ui/no-image-185x278.jpg" );
                    }
                    else {
                        serie.setPoster_path(
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/" + serie.getPoster_path() );
                    }
                    if ( serie.getOverview().length() >= cut ) {
                        serie.setShort_Overview( serie.getOverview().substring( 0, cut ) + "..." );
                    }
                    else {
                        serie.setShort_Overview( serie.getOverview() );
                    }
                    serie.setTheSerieDbId( serie.getId() );

                    // For Season
                    List<Season> v = serie.getSeasons();
                    while ( a != v.size() + 1 ) {
                        Season season = v.get( a );
                        urlSeason = new URL(
                            "https://api.themoviedb.org/3/tv/" + id + "/season/" + a + "?api_key=" + THEMOVIEDB_API_KEY
                                + "&language="
                                + THEMOVIEDB_LANG );
                        try (BufferedReader readerSeason =
                            new BufferedReader( new InputStreamReader( urlSeason.openStream(), "UTF-8" ) )) {
                            String jsonTextSeason = readAll( readerSeason );

                            Season seasonJson = jsonMapperSeason.readValue( jsonTextSeason, Season.class );
                            if ( season != null ) {
                                season.setAir_date( seasonJson.getAir_date() );
                                season.setName( seasonJson.getName() );
                                season.setOverview( seasonJson.getOverview() );
                                season.setPoster_path( seasonJson.getPoster_path() );
                                season.setSeason_number( seasonJson.getSeason_number() );
                                season.setSerie_id( serie );
                                season.setEpisodes( seasonJson.getEpisodes() );
                                if ( season.getOverview().length() >= cut ) {
                                    season.setShort_Overview( seasonJson.getOverview().substring( 0, cut ) + "..." );
                                }
                                else {
                                    season.setShort_Overview( seasonJson.getOverview() );
                                }

                                v.set( a, season );
                                // For Episodes
                                List<Episode> e = season.getEpisodes();
                                int b = 0;
                                while ( b != e.size() ) {
                                    Episode episode = e.get( b );
                                    urlEpisode = new URL(
                                        "https://api.themoviedb.org/3/tv/" + id + "/season/" + a + "/episode/"
                                            + b + "?api_key="
                                            + THEMOVIEDB_API_KEY
                                            + "&language="
                                            + THEMOVIEDB_LANG );
                                    BufferedReader readerEpisode =
                                        new BufferedReader( new InputStreamReader( urlEpisode.openStream(), "UTF-8" ) );
                                    String jsonTextEpisode = readAll( readerEpisode );

                                    Episode episodeJson = jsonMapperEpisode.readValue( jsonTextEpisode, Episode.class );
                                    if ( episode != null ) {
                                        episode.setAir_date( episodeJson.getAir_date() );
                                        episode.setName( episodeJson.getName() );
                                        episode.setOverview( episodeJson.getOverview() );
                                        episode.setEpisode_number( episodeJson.getEpisode_number() );
                                        episode.setSeason_id( season );
                                        episode.setSeason_number( episodeJson.getSeason_number() );
                                        if ( episode.getName().isEmpty() ) {
                                            episode.setName( "Es gibt zurzeit kein Title" );
                                        }
                                        if ( episode.getOverview().length() >= cut ) {
                                            episode
                                                .setShort_Overview( episodeJson.getOverview().substring( 0, cut ) + "..." );
                                        }
                                        else {
                                            episode.setShort_Overview( episodeJson.getOverview() );
                                        }
                                        e.set( b, episode );
                                    }
                                    b++;
                                }
                                season.setEpisodes( e );
                            }
                            a++;
                        }
                        catch ( IOException e ) {
                            System.out.println( "Fehler: " + e );
                            a++;
                        }
                    }
                    serie.setSeasons( v );
                }

                Serie savedSerie = serieDao.doSave( serie, errors );
                if ( savedSerie != null ) {
                    MessageHelper.addSuccessAttribute( ra,
                        "common.message.success_create" );
                    return "redirect:/serie/" + savedSerie.getId();
                }
                else {
                    MessageHelper.addErrorAttribute( ra, "nope" );
                    return "serie/list";
                }
            }
            catch ( IOException e ) {
                System.out.println( "Fehler: " + e );
            }
        }

        done = true;

        return "movie/list";

    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/add/{id}", method = RequestMethod.GET )
    public String addSeries( @PathVariable Long id, @Valid Serie serie, Errors errors,
                             RedirectAttributes ra, Model model ) {
        URL url = null;
        serie = null;
        int cut = 100;
        ObjectMapper jsonMapperSerie = new ObjectMapper();
        jsonMapperSerie.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        errors = null;
        boolean done = false;
        while ( !done ) {
            try {
                url = new URL(
                    "https://api.themoviedb.org/3/tv/" + id + "?api_key=" + THEMOVIEDB_API_KEY + "&language=" + THEMOVIEDB_LANG );
                BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), "UTF-8" ) );
                String jsonText = readAll( reader );
                serie = jsonMapperSerie.readValue( jsonText, Serie.class );
                if ( serie != null ) {
                    for ( Genres s : serie.getGenres() ) {
                        if ( genreDao.findOneByName( s.getName() ) != null ) {
                            s.setId(
                                genreDao.findOneByName( s.getName() ).getId() );
                            s.setCreatedBy( genreDao.findOneByName( s.getName() ).getCreatedBy() );
                            s.setCreatedDate( genreDao.findOneByName( s.getName() ).getCreatedDate() );
                            s.setLastModifiedBy( genreDao.findOneByName( s.getName() ).getLastModifiedBy() );
                            s.setLastModifiedDate(
                                genreDao.findOneByName( s.getName() ).getLastModifiedDate() );
                        }
                    }
                    if ( serie.getOverview() == null ||
                        serie.getOverview().isEmpty() ) {
                        serie.setOverview( "Zurzeit gibt es keine Beschreibung" );
                    }
                    if ( serie.getName() == null || serie.getName().isEmpty() ) {
                        serie.setName( "Zurzeit gibt es kein Title" );
                    }
                    if ( serie.getPoster_path() == null || serie.getPoster_path().isEmpty() ) {
                        serie.setPoster_path(
                            "http://manntheatres.com/images/ui/no-image-185x278.jpg" );
                    }
                    else {
                        serie.setPoster_path(
                            "https://image.tmdb.org/t/p/w185_and_h278_bestv2/" + serie.getPoster_path() );
                    }
                    if ( serie.getOverview().length() >= cut ) {
                        serie.setShort_Overview( serie.getOverview().substring( 0, cut ) + "..." );
                    }
                    else {
                        serie.setShort_Overview( serie.getOverview() );
                    }
                    serie.setTheSerieDbId( serie.getId() );

                    // Season
                    List<Season> season = addSeason( serie );
                    serie.setSeasons( season );
                }
            }
            catch ( IOException e ) {
                System.out.println( "Fehler: " + e );
            }
            done = true;
            Serie savedSerie = serieDao.doSave( serie, errors );
            if ( savedSerie != null ) {
                MessageHelper.addSuccessAttribute( ra,
                    "common.message.success_create" );
                return "redirect:/serie/" + savedSerie.getId();
            }
            else {
                MessageHelper.addErrorAttribute( ra, "nope" );
                return "serie/list";
            }

        }
        return "movie/list";

    }

    private List<Season> addSeason( Serie serie ) {
        ObjectMapper jsonMapperSeason = new ObjectMapper();
        jsonMapperSeason.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        URL urlSeason = null;
        int cut = 100;
        for ( int i = 0; i <= serie.getSeasons().size() - 1; i++ ) {

            Season season = serie.getSeasons().get( i );

            try {
                urlSeason = new URL(
                    "https://api.themoviedb.org/3/tv/" + serie.getTheSerieDbId() + "/season/" + season.getSeason_number() + "?api_key="
                        + THEMOVIEDB_API_KEY
                        + "&language="
                        + THEMOVIEDB_LANG );

                BufferedReader readerSeason =
                    new BufferedReader( new InputStreamReader( urlSeason.openStream(), "UTF-8" ) );
                String jsonTextSeason = readAll( readerSeason );

                Season seasonJson = jsonMapperSeason.readValue( jsonTextSeason, Season.class );

                if ( season != null ) {
                    season.setAir_date( seasonJson.getAir_date() );
                    season.setName( seasonJson.getName() );
                    season.setOverview( seasonJson.getOverview() );
                    season.setPoster_path( seasonJson.getPoster_path() );
                    season.setSeason_number( seasonJson.getSeason_number() );
                    season.setSerie_id( serie );
                    season.setEpisodes( seasonJson.getEpisodes() );
                    if ( season.getOverview().length() >= cut ) {
                        season.setShort_Overview( seasonJson.getOverview().substring( 0, cut ) + "..." );
                    }
                    else {
                        season.setShort_Overview( seasonJson.getOverview() );
                    }

                    serie.getSeasons().set( i, season );
                }
                List<Episode> episode = addEpisode( season, serie );
                season.setEpisodes( episode );
            }
            catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return serie.getSeasons();
    }

    private List<Episode> addEpisode( Season season, Serie serie ) {
        ObjectMapper jsonMapperEpisode = new ObjectMapper();
        jsonMapperEpisode.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        URL urlEpisode = null;
        int cut = 100;
        for ( int i = 0; i <= season.getEpisodes().size() - 1; i++ ) {

            Episode episode = season.getEpisodes().get( i );

            try {
                urlEpisode = new URL(
                    "https://api.themoviedb.org/3/tv/" + serie.getTheSerieDbId() + "/season/" + season.getSeason_number() + "/episode/"
                        + episode.getEpisode_number() + "?api_key="
                        + THEMOVIEDB_API_KEY
                        + "&language="
                        + THEMOVIEDB_LANG );

                BufferedReader readerEpisode =
                    new BufferedReader( new InputStreamReader( urlEpisode.openStream(), "UTF-8" ) );
                String jsonTextEpisode = readAll( readerEpisode );

                Episode episodeJson = jsonMapperEpisode.readValue( jsonTextEpisode, Episode.class );

                if ( episode != null ) {
                    episode.setAir_date( episodeJson.getAir_date() );
                    episode.setName( episodeJson.getName() );
                    if ( episodeJson.getOverview() == null || episodeJson.getOverview().isEmpty() ) {
                        episode.setOverview( "Es gibt zurzeit keine Beschreibung" );
                    }
                    else {
                        episode.setOverview( episodeJson.getOverview() );
                    }

                    episode.setEpisode_number( episodeJson.getEpisode_number() );
                    episode.setSeason_id( season );
                    episode.setSeason_number( episodeJson.getSeason_number() );
                    if ( episode.getName().isEmpty() ) {
                        episode.setName( "Es gibt zurzeit kein Title" );
                    }
                    if ( episode.getOverview().length() >= cut ) {
                        episode
                            .setShort_Overview( episodeJson.getOverview().substring( 0, cut ) + "..." );
                    }
                    else {
                        episode.setShort_Overview( episodeJson.getOverview() );
                    }
                    season.getEpisodes().set( i, episode );
                }
            }
            catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return season.getEpisodes();
    }

    private static String readAll( Reader rd )
        throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ( ( cp = rd.read() ) != -1 ) {
            sb.append( (char) cp );
        }
        return sb.toString();
    }

}
