package com.miyava.common;

/**
 * This class implements one breadcrumb.
 */
public class BreadCrumb {

    /**
     * link to place in hierarchy. Can be null for last crumb.
     */
    private String link;

    /**
     * The text for the link.
     */
    private String linktext;

    /**
     * The icon for the crumb. (css icon class)
     */
    private String icon;

    public BreadCrumb( String link, String linktext ) {
        this( link, linktext, null );
    }

    public BreadCrumb( String link, String linktext, String icon ) {
        this.link = link;
        this.linktext = linktext;
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    /**
     * @return the linktext
     */
    public String getLinktext() {
        return linktext;
    }

    /**
     * @param linktext the linktext to set
     */
    public void setLinktext( String linktext ) {
        this.linktext = linktext;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon( String icon ) {
        this.icon = icon;
    }

    /**
     * @param link the link to set
     */
    public void setLink( String link ) {
        this.link = link;
    }

}
