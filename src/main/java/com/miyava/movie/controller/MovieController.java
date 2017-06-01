package com.miyava.movie.controller;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miyava.common.AbstractController;
import com.miyava.genres.model.Genres;
import com.miyava.genres.service.GenresDao;
import com.miyava.movie.model.Movie;
import com.miyava.movie.model.UserMovie;
import com.miyava.movie.service.MovieDao;
import com.miyava.user.model.User;
import com.miyava.user.service.UserDao;
import com.miyava.util.AjaxUtils;
import com.miyava.util.BreadCrumbs;
import com.miyava.util.MessageHelper;

@JsonIgnoreProperties( ignoreUnknown = true )
@Controller
@RequestMapping( MovieController.BASE_URL )
public class MovieController
    extends AbstractController {

    protected final static String BASE_URL = "/movie";

    protected final static String CREATE_FORM = "movie/createForm";

    private static final String MOVIE_VIEW_NAME = "movie/editForm";

    private static final String MOVIE_TAB_VIEW_NAME = "movie/movietabs";

    private static final java.util.Date Date = null;

    private static final String THEMOVIEDB_API_KEY = "e32412fcd041dff3927fd5c7c5498600";

    private static final String THEMOVIEDB_LANG = "de_DE";

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private GenresDao genreDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public MovieController() {
        super( BASE_URL );
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( method = RequestMethod.GET )
    public String dashboard( Model model ) {
        BreadCrumbs.set( model, "movie.page.list.breadcrumb" );
        return "movie/movie";
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String show( @PathVariable Long id, Model model ) {
        model.addAttribute( "movie", movieDao.findOne( id ) );
        return MOVIE_VIEW_NAME;
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/{id}/maintab", method = RequestMethod.GET )
    public String mainTab( @PathVariable Long id, Model model,
                           @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {
        String GenreName = "";
        Movie movie = movieDao.findOne( id );
        model.addAttribute( "movie", movie );

        for ( Genres s : movieDao.findOne( id ).getGenres() ) {
            GenreName += "" + s.getId() + ",";
        }

        List<Genres> Genres = new ArrayList<Genres>();
        for ( Genres s : genreDao.findAll() ) {
            Genres.add( s );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        String watched = "";

        if ( !currentUser.getUserMovies().isEmpty() ) {

            for ( UserMovie s : currentUser.getUserMovies() ) {
                if ( s.getUser().getId() == currentUser.getId() ) {
                    if ( s.getMovie().getId() == id ) {
                        watched = "Gesehen am: " + s.getWatchedDate();
                    }
                    else {
                        watched = "Noch nicht gesehen";
                    }
                }
            }
        }
        else {
            watched = "Noch nicht gesehen";
        }
        model.addAttribute( "userMovieWatched", watched );
        model.addAttribute( "genres", Genres );
        model.addAttribute( "selectgenres", GenreName );

        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return MOVIE_TAB_VIEW_NAME.concat( " :: maintab" );
        }
        else {
            return MOVIE_VIEW_NAME;
        }
    }

    @RequestMapping( value = "/{id}/update", method = RequestMethod.POST )
    public String update( @PathVariable Long id, Model model,
                          @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );
        Movie movie = movieDao.findOne( id );
        Boolean save = false;
        if ( currentUser.getUserMovies().isEmpty() ) {
            save = true;
        }
        else {
            for ( UserMovie s : currentUser.getUserMovies() ) {
                if ( s.getMovie().getId() != movie.getId() ) {
                    save = true;
                }
                else {
                    save = false;
                    break;
                }
            }
        }
        if ( save ) {
            java.util.Date dt = new java.util.Date();

            java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat( "yyyy-MM-dd" );

            String currentTime = sdf.format( dt );

            String sql = "INSERT INTO user_movie(movie_id,user_id,watched_date) VALUES(?,?,?)";
            jdbcTemplate.update( sql, movie.getId(), currentUser.getId(), currentTime );
        }

        model.addAttribute( "movie", movieDao.findOne( id ) );

        return "redirect:/movie/" + movie.getId();
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/{id}/descriptiontab", method = RequestMethod.GET )
    public String listTab( @PathVariable Long id, Model model,
                           @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {

        Movie movie = movieDao.findOne( id );
        model.addAttribute( "movie", movie );

        if ( AjaxUtils.isAjaxRequest( requestedWith ) ) {
            return MOVIE_TAB_VIEW_NAME.concat( " :: descriptiontab" );
        }
        else {
            return MOVIE_VIEW_NAME;
        }
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( "/new" )
    public String createForm( Model model ) {
        BreadCrumbs.set( model, "movie", "movie.page.list.breadcrumb", "movie.page.create.breadcrumb" );
        model.addAttribute( "movie", new Movie() );
        return CREATE_FORM;
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/create", method = RequestMethod.POST )
    public String createAction( @Valid Movie movie, Errors errors, RedirectAttributes ra, Model model ) {
        if ( errors.hasErrors() ) {
            return CREATE_FORM;
        }

        Movie savedMovie = movieDao.doSave( movie, errors );
        if ( savedMovie != null ) {
            MessageHelper.addSuccessAttribute( ra, "common.message.success_create" );
            return "redirect:/movie/" + savedMovie.getId();
        }
        else {
            MessageHelper.addGlobalCreateErrorAttribute( model, errors );
            return CREATE_FORM;
        }
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/add/all" )
    public String addAllMovies( @Valid Movie movie, Errors errors, RedirectAttributes ra, Model model ) {
        URL allmoviesUrl = null;
        URL url = null;
        movie = null;
        Long LastMovieId = null;
        String message = "";
        String MovieTitle = "";
        int countAllMovies = 0;
        int cut = 100;

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        ObjectMapper jsonMapperMovie = new ObjectMapper();
        jsonMapperMovie.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        try {
            allmoviesUrl =
                new URL( "https://api.themoviedb.org/3/movie/latest?api_key=" + THEMOVIEDB_API_KEY + "&language=" + THEMOVIEDB_LANG );
            try (BufferedReader reader = new BufferedReader( new InputStreamReader( allmoviesUrl.openStream(), "UTF-8" ) )) {
                String jsonText = readAll( reader );
                movie = jsonMapper.readValue( jsonText, Movie.class );
                if ( movie != null ) {
                    LastMovieId = movie.getId();
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
        if ( LastMovieId != 0L ) {
            for ( int i = 0; i <= Math.toIntExact( LastMovieId ); i++ ) {
                errors = null;
                try {
                    url = new URL(
                        "https://api.themoviedb.org/3/movie/" + i + "?api_key=" + THEMOVIEDB_API_KEY + "&language=" + THEMOVIEDB_LANG );
                    try (BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), "UTF-8" ) )) {
                        String jsonText = readAll( reader );
                        movie = jsonMapper.readValue( jsonText, Movie.class );
                        if ( movie != null ) {

                            if ( movieDao.findOneByTitle( movie.getTitle() ) != null ) {
                                MovieTitle = movieDao.findOneByTitle( movie.getTitle() ).getTitle();
                            }
                            else {
                                MovieTitle = "";
                            }

                            if ( movie.getTitle().equals( MovieTitle ) ) {
                                message = "Den Film gibt es schon";
                                // break;
                            }
                            else {
                                for ( Genres s : movie.getGenres() ) {
                                    if ( genreDao.findOneByName( s.getName() ) != null ) {
                                        s.setId( genreDao.findOneByName( s.getName() ).getId() );
                                        s.setCreatedBy( genreDao.findOneByName( s.getName() ).getCreatedBy() );
                                        s.setCreatedDate( genreDao.findOneByName( s.getName() ).getCreatedDate() );
                                        s.setLastModifiedBy( genreDao.findOneByName( s.getName() ).getLastModifiedBy() );
                                        s.setLastModifiedDate( genreDao.findOneByName( s.getName() ).getLastModifiedDate() );
                                    }
                                }

                                if ( movie.getOverview() == null || movie.getOverview().isEmpty() ) {
                                    movie.setOverview( "Zurzeit gibt es keine Beschreibung" );
                                }

                                if ( movie.getTitle() == null || movie.getTitle().isEmpty() ) {
                                    movie.setTitle( "Zurzeit gibt es kein Title" );
                                }

                                if ( movie.getPoster_path() == null || movie.getPoster_path().isEmpty() ) {
                                    movie.setPoster_path( "http://manntheatres.com/images/ui/no-image-185x278.jpg" );
                                }
                                else {
                                    movie.setPoster_path( "https://image.tmdb.org/t/p/w185_and_h278_bestv2/" + movie.getPoster_path() );
                                }

                                if ( movie.getRuntime() == null || movie.getRuntime().isEmpty() ) {
                                    movie.setRuntime( "0" );
                                }

                                if ( movie.getOverview().length() >= cut ) {

                                    movie.setShortOverview( movie.getOverview().substring( 0, cut ) + "..." );
                                }
                                else {
                                    movie.setShortOverview( movie.getOverview() );
                                }

                                Movie savedMovie = movieDao.doSave( movie, errors );
                                if ( savedMovie != null ) {
                                    countAllMovies++;
                                }
                                else {
                                    message = "Es ist ein Fehler aufgetreten.";
                                    break;
                                }
                            }
                        }

                    }
                    catch ( IOException e ) {
                        // e.printStackTrace();
                    }
                }
                catch ( MalformedURLException e ) {
                    e.printStackTrace();
                }
            }
        }

        if ( message == "" ) {
            message = "Es wurden" + countAllMovies + "hinzugefügt";
        }

        MessageHelper.addSuccessAttribute( ra, message );
        return "redirect:/movies";
    }

    @Secured( "ROLE_ADMIN" )
    @RequestMapping( value = "/add/{id}", method = RequestMethod.GET )
    public String addMovies( @PathVariable Long id, @Valid Movie movie, Errors errors, RedirectAttributes ra, Model model ) {
        URL url = null;
        movie = null;
        int cut = 100;

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        errors = null;
        try {
            url = new URL( "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + THEMOVIEDB_API_KEY + "&language=" + THEMOVIEDB_LANG );
            try (BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), "UTF-8" ) )) {
                String jsonText = readAll( reader );
                movie = jsonMapper.readValue( jsonText, Movie.class );
                if ( movie != null ) {

                    for ( Genres s : movie.getGenres() ) {

                        if ( genreDao.findOneByName( s.getName() ) != null ) {
                            s.setId( genreDao.findOneByName( s.getName() ).getId() );
                            s.setCreatedBy( genreDao.findOneByName( s.getName() ).getCreatedBy() );
                            s.setCreatedDate( genreDao.findOneByName( s.getName() ).getCreatedDate() );
                            s.setLastModifiedBy( genreDao.findOneByName( s.getName() ).getLastModifiedBy() );
                            s.setLastModifiedDate( genreDao.findOneByName( s.getName() ).getLastModifiedDate() );
                        }
                    }

                    if ( movie.getOverview() == null || movie.getOverview().isEmpty() ) {
                        movie.setOverview( "Zurzeit gibt es keine Beschreibung" );
                    }

                    if ( movie.getTitle() == null || movie.getTitle().isEmpty() ) {
                        movie.setTitle( "Zurzeit gibt es kein Title" );
                    }

                    if ( movie.getPoster_path() == null || movie.getPoster_path().isEmpty() ) {
                        movie.setPoster_path( "http://manntheatres.com/images/ui/no-image-185x278.jpg" );
                    }
                    else {
                        movie.setPoster_path( "https://image.tmdb.org/t/p/w185_and_h278_bestv2/" + movie.getPoster_path() );
                    }

                    if ( movie.getRuntime() == null || movie.getRuntime().isEmpty() ) {
                        movie.setRuntime( "0" );
                    }

                    if ( movie.getOverview().length() >= cut ) {

                        movie.setShortOverview( movie.getOverview().substring( 0, cut ) + "..." );
                    }
                    else {
                        movie.setShortOverview( movie.getOverview() );
                    }

                    Movie savedMovie = movieDao.doSave( movie, errors );
                    if ( savedMovie != null ) {
                        MessageHelper.addSuccessAttribute( ra, "common.message.success_create" );
                        return "redirect:/movie/" + savedMovie.getId();

                    }
                    else {
                        MessageHelper.addErrorAttribute( ra, "nope" );
                        return "movie/list";
                    }
                }

            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
        return "movie/list";
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
