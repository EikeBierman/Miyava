package com.miyava.home;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.miyava.common.AbstractController;
import com.miyava.genres.model.Genres;
import com.miyava.movie.model.Movie;
import com.miyava.movie.model.UserMovie;
import com.miyava.movie.service.MovieDao;
import com.miyava.serie.model.Serie;
import com.miyava.serie.model.UserSerie;
import com.miyava.serie.service.SerieDao;
import com.miyava.user.model.User;
import com.miyava.user.service.UserDao;
import com.miyava.util.BreadCrumbs;

@JsonIgnoreProperties( ignoreUnknown = true )
@Controller
@RequestMapping( HomeController.BASE_URL )
public class HomeController
    extends AbstractController {

    protected final static String BASE_URL = "/";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private SerieDao serieDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public HomeController() {
        super( BASE_URL );
    }

    @RequestMapping( value = { BASE_URL, "home" } )
    public String home( Model model ) {
        return "home/home";
    }

    public void setDataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return DataSourceUtils.getConnection( dataSource );
    }

    @RequestMapping( value = ( "/movies" ) )
    public String Movie( Model model, HttpServletRequest request ) {
        request.getSession().setAttribute( "movie", null );

        return "redirect:/movies/page/1";
    }

    @RequestMapping( value = "/movies/page/{pageNumber}/movie/{id}/update", method = org.springframework.web.bind.annotation.RequestMethod.POST )
    public String moviePageUpdate( HttpServletRequest request, @PathVariable Integer pageNumber, @PathVariable Long id, Model model,
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

        return "redirect:/movies/page/" + pageNumber;
    }

    @SuppressWarnings( "null" )
    @RequestMapping( value = "/movies/page/{pageNumber}", method = org.springframework.web.bind.annotation.RequestMethod.GET )
    public String showPagedMoviePage( HttpServletRequest request, @PathVariable Integer pageNumber, Model model,
                                      @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute( "movie" );

        int MOVIE_LIST_PAGE_SIZE = 10;
        List<Movie> movies = null;
        for ( Movie s : movieDao.findAll( pageNumber, MOVIE_LIST_PAGE_SIZE ) ) {
            movies.add( s );
        }
        // List<Movie> movies = movieDao.findAll( pageNumber, MOVIE_LIST_PAGE_SIZE );

        if ( pagedListHolder == null ) {
            pagedListHolder = new PagedListHolder<>( movies );
            pagedListHolder.setPageSize( MOVIE_LIST_PAGE_SIZE );
        }
        else {
            final int goToPage = pageNumber - 1;
            if ( goToPage <= pagedListHolder.getPageCount() && goToPage >= 0 ) {
                pagedListHolder.setPage( goToPage );
            }
        }

        request.getSession().setAttribute( "movie", pagedListHolder );

        pagedListHolder.setSort( new MutableSortDefinition( "release_date", true, false ) );
        pagedListHolder.resort();

        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max( 1, current - MOVIE_LIST_PAGE_SIZE );
        int end = Math.min( begin + 5, pagedListHolder.getPageCount() );
        int totalPageCount = pagedListHolder.getPageCount();
        String baseUrl = "/movies/page/";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        List<UserMovie> UserWatched = currentUser.getUserMovies();

        List<Movie> movieUserWatched = new ArrayList<Movie>();

        for ( UserMovie u : UserWatched ) {
            movieUserWatched.add( u.getMovie() );
        }

        String genreName = "";
        for ( Movie s : movieDao.findAll() ) {
            for ( Genres g : s.getGenres() ) {
                genreName += "" + g.getName() + ", ";
            }
        }

        genreName = genreName.substring( 0, genreName.length() - 1 );

        model.addAttribute( "genre", genreName );
        model.addAttribute( "movieUserWatched", movieUserWatched );

        model.addAttribute( "beginIndex", begin );
        model.addAttribute( "endIndex", end );
        model.addAttribute( "currentIndex", current );
        model.addAttribute( "totalPageCount", totalPageCount );
        model.addAttribute( "baseUrl", baseUrl );
        model.addAttribute( "movies", pagedListHolder );

        return "/home/movies";
    }

    @RequestMapping( value = ( "/movies/{id}" ) )
    public String ShowMovie( @PathVariable Long id, Model model ) {

        Movie movie = movieDao.findOne( id );

        String genre = "";
        for ( Genres g : movie.getGenres() ) {
            genre += "" + g.getName() + ", ";
        }

        if ( genre.length() > 0 ) {
            genre = genre.substring( 0, genre.length() - 1 );
        }
        model.addAttribute( "movie", movie );
        model.addAttribute( "genre", genre );

        return "home/showMovie";
    }

    @RequestMapping( value = ( "/series" ) )
    public String Serie( Model model, HttpServletRequest request ) {
        request.getSession().setAttribute( "serie", null );

        return "redirect:/series/page/1";
    }

    @RequestMapping( value = "/series/page/{pageNumber}", method = org.springframework.web.bind.annotation.RequestMethod.GET )
    public String showPagedSeriePage( HttpServletRequest request, @PathVariable Integer pageNumber, Model model,
                                      @RequestHeader( value = "X-Requested-With", required = false ) String requestedWith ) {
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute( "serie" );

        int SERIE_LIST_PAGE_SIZE = 10;

        List<Serie> series = serieDao.getSeries();

        if ( pagedListHolder == null ) {
            pagedListHolder = new PagedListHolder<>( series );
            pagedListHolder.setPageSize( SERIE_LIST_PAGE_SIZE );
        }
        else {
            final int goToPage = pageNumber - 1;
            if ( goToPage <= pagedListHolder.getPageCount() && goToPage >= 0 ) {
                pagedListHolder.setPage( goToPage );
            }
        }

        request.getSession().setAttribute( "serie", pagedListHolder );

        pagedListHolder.setSort( new MutableSortDefinition( "release_date", true, false ) );
        pagedListHolder.resort();

        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max( 1, current - SERIE_LIST_PAGE_SIZE );
        int end = Math.min( begin + 5, pagedListHolder.getPageCount() );
        int totalPageCount = pagedListHolder.getPageCount();
        String baseUrl = "/series/page/";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        List<UserSerie> UserWatched = currentUser.getUserSeries();

        List<Serie> serieUserWatched = new ArrayList<Serie>();

        for ( UserSerie u : UserWatched ) {
            serieUserWatched.add( u.getSerie() );
        }

        /*
         * String genreName = ""; for ( Serie s : serieDao.findAll() ) { for ( Genres g : s.getGenres() ) { genreName +=
         * "" + g.getName() + ", "; } } genreName = genreName.substring( 0, genreName.length() - 1 );
         */

        /* model.addAttribute( "genre", genreName ); */
        model.addAttribute( "serieUserWatched", serieUserWatched );

        model.addAttribute( "beginIndex", begin );
        model.addAttribute( "endIndex", end );
        model.addAttribute( "currentIndex", current );
        model.addAttribute( "totalPageCount", totalPageCount );
        model.addAttribute( "baseUrl", baseUrl );
        model.addAttribute( "series", pagedListHolder );

        return "/home/series";
    }

    @RequestMapping( value = ( "/search" ) )
    public String search( @RequestParam( value = "search", required = true ) String search, Model model ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        Connection conn = getConnection();
        String searchTerm = "'%" + search + "%'";
        String sql =
            "SELECT * FROM movie WHERE title LIKE " + searchTerm;
        List<Movie> movies = new ArrayList<Movie>();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( sql );
            while ( rs.next() ) {
                movies.add( movieDao.findOne( rs.getLong(
                    "movie_id" ) ) );
            }
            rs.close();
            stmt.close();
        }
        catch ( SQLException e ) {
            e.printStackTrace();
        }

        List<UserMovie> UserWatched = currentUser.getUserMovies();

        List<Movie> movieUserWatched = new ArrayList<Movie>();

        for ( UserMovie u : UserWatched ) {
            movieUserWatched.add( u.getMovie() );
        }

        model.addAttribute( "movies", movies );
        model.addAttribute( "movieUserWatched", movieUserWatched );

        return "home/search";
    }

    @RequestMapping( value = ( "/index" ) )
    public String Index() {
        return "home/index";
    }

    @RequestMapping( value = { "/statistic/movie" } )
    public String statistic( Model model ) {
        BreadCrumbs.set( model );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        String sql = "SELECT COUNT(*) FROM  user_movie WHERE user_id = ?";
        String movieUserCount = jdbcTemplate.queryForObject( sql, String.class, currentUser.getId() );

        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat( "yyyy-MM-dd" );

        String currentTime = sdf.format( dt );

        String sql2 = "select count(*) from movie WHERE release_date <= ?";
        String movieCount = jdbcTemplate.queryForObject( sql2, String.class, currentTime );

        float movieUserProcent = ( Float.parseFloat( movieUserCount ) / Float.parseFloat( movieCount ) ) * 100;
        int movieUserTime = 0;
        for ( UserMovie s : currentUser.getUserMovies() ) {
            movieUserTime += Integer.parseInt( s.getMovie().getRuntime() );
        }

        model.addAttribute( "movieUserCount", movieUserCount );
        model.addAttribute( "movieCount", movieCount );
        model.addAttribute( "movieUserProcent", movieUserProcent );
        model.addAttribute( "movieUserTime", movieUserTime );

        return "home/statistic/movie";
    }

    @RequestMapping( value = { "/login" } )
    public String login() {
        return "home/login";
    }

    @RequestMapping( value = "/403" )
    public String Error403() {
        return "home/403";
    }

}
