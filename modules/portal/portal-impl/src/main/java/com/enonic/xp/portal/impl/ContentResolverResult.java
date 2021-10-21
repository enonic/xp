package com.enonic.xp.portal.impl;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;

public final class ContentResolverResult
{
    private final Content content;

    private final Site nearestSite;

    private final String siteRelativePath;

    private final String notFoundHint;

    ContentResolverResult( final Content content, final Site nearestSite, final String siteRelativePath, final String notFoundHint )
    {
        this.content = content;
        this.nearestSite = nearestSite;
        this.siteRelativePath = siteRelativePath;
        this.notFoundHint = notFoundHint;
    }

    static ContentResolverResult nothingFound( final String notFoundHint )
    {
        return new ContentResolverResult( null, null, null, notFoundHint );
    }

    static ContentResolverResult noSiteFound( final Content content, final String siteNotFoundHint )
    {
        return new ContentResolverResult( content, null, null, siteNotFoundHint );
    }

    static ContentResolverResult build( final Content content, final Site site, String contentPath, final String notFoundHint )
    {
        final String siteRelativePath;
        if ( site == null )
        {
            siteRelativePath = null;
        }
        else
        {
            final String sitePath = site.getPath().toString();
            if ( sitePath.equals( contentPath ) )
            {
                siteRelativePath = "/";
            }
            else
            {
                siteRelativePath = contentPath.substring( sitePath.length() );
            }
        }
        return new ContentResolverResult( content, site, siteRelativePath, notFoundHint );
    }

    public Content getContent()
    {
        return content;
    }

    public Content getContentOrElseThrow()
    {
        if ( content != null )
        {
            return content;
        }
        else
        {
            throw WebException.notFound( String.format( "Page [%s] not found", notFoundHint ) );
        }
    }

    public Site getNearestSite()
    {
        return nearestSite;
    }

    public Site getNearestSiteOrElseThrow()
    {
        if ( nearestSite != null )
        {
            return nearestSite;
        }
        else
        {
            throw WebException.notFound( String.format( "Site for [%s] not found", notFoundHint ) );
        }
    }

    public String getSiteRelativePath()
    {
        return siteRelativePath;
    }
}
