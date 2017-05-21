package com.miyava.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;

import com.miyava.common.BreadCrumb;

/**
 * Generate the bread crumbs.
 */
public class BreadCrumbs {

    /**
     * Toplevel of this site.
     */
    private static BreadCrumb homeCrumb;

    // To prevent initialisations.
    private BreadCrumbs() {}

    static {
        homeCrumb = new BreadCrumb( "/", "common.menu.dashboard", "fa-dashboard" );
    }

    /**
     * Convenience method to set a breadcrumb on a top level page.
     */
    public static void set( Model model, String lastcrumb ) {
        BreadCrumbs.set( model, new BreadCrumb( null, lastcrumb ) );
    }

    /**
     * Convenience method to set a breadcrumb on a page one level below.
     */
    public static void set( Model model, String parentLink, String parentText, String lastcrumb ) {
        BreadCrumbs.set( model, new BreadCrumb( parentLink, parentText ), new BreadCrumb( null, lastcrumb ) );
    }

    /**
     * Create a indefinite list of breadcrumbs.
     */
    public static void set( Model model, BreadCrumb... crumbs ) {
        List<BreadCrumb> breadcrumbList = new ArrayList<BreadCrumb>();

        breadcrumbList.add( homeCrumb );

        for ( BreadCrumb crumb : crumbs ) {
            breadcrumbList.add( crumb );
        }
        model.addAttribute( "breadcrumbs", breadcrumbList );
    }

}
