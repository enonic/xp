package com.enonic.xp.portal.impl;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;

public final class ContentResolverResult
{
    private final Content content;

    private final Site nearestSite;

    private final Content project;

    private final String siteRelativePath;

    private final String notFoundHint;

    private final boolean contentExists;

    ContentResolverResult( final Content content, final boolean contentExists, final Site nearestSite, final Content project, final String siteRelativePath,
                           final String notFoundHint )
    {
        this.content = content;
        this.nearestSite = nearestSite;
        this.project = project;
        this.siteRelativePath = siteRelativePath;
        this.notFoundHint = notFoundHint;
        this.contentExists = contentExists;
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
        else if ( contentExists )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", notFoundHint ) );
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

    public Content getProject()
    {
        return project;
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
