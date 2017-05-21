package com.miyava.home;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miyava.common.AbstractController;
import com.miyava.genres.model.Genres;
import com.miyava.movie.model.Movie;
import com.miyava.movie.model.UserMovie;
import com.miyava.movie.service.MovieDao;
import com.miyava.user.model.User;
import com.miyava.user.service.UserDao;
import com.miyava.util.BreadCrumbs;

@Controller
@RequestMapping( HomeController.BASE_URL )
public class HomeController
    extends AbstractController {

    protected final static String BASE_URL = "/";

    @Autowired
    private UserDao userDao;

    @Autowired
    private MovieDao movieDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public HomeController() {
        super( BASE_URL );
    }

    @RequestMapping( value = { BASE_URL, "home" } )
    public String home( Model model ) {
        return "home/home";
    }

    @RequestMapping( value = ( "/movies" ) )
    public String Movie( Model model ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDao.findOneByUsername( auth.getName() );

        Iterable<Movie> movie = movieDao.findAll();

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
        model.addAttribute( "movies", movie );
        model.addAttribute( "movieUserWatched", movieUserWatched );
        model.addAttribute( "userWatched", UserWatched );

        return "home/movies";
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
