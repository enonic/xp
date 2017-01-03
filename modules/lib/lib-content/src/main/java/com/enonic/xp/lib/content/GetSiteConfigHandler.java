package com.enonic.xp.lib.content;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.site.Site;

public final class GetSiteConfigHandler
    extends BaseContextHandler
{
    private String key;

    private String applicationKey;

    @Override
    protected Object doExecute()
    {
        if ( this.key == null || this.key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }
        if ( this.key.startsWith( "/" ) )
        {
            return getByPath( ContentPath.from( this.key ) );
        }
        else
        {
            return getById( ContentId.from( this.key ) );
        }
    }

    private PropertyTreeMapper getByPath( final ContentPath contentPath )
    {
        try
        {
            final Content content = this.contentService.getByPath( contentPath );
            return getById( content.getId() );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private PropertyTreeMapper getById( final ContentId contentId )
    {
        try
        {
            final Site site = this.contentService.getNearestSite( contentId );
            if ( site != null && applicationKey != null )
            {
                final PropertyTree siteConfigPropertyTree = site.getSiteConfig( ApplicationKey.from( applicationKey ) );
                if ( siteConfigPropertyTree != null )
                {
                    return new PropertyTreeMapper( siteConfigPropertyTree );
                }
            }
        }
        catch ( final ContentNotFoundException e )
        {
        }
        return null;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setApplicationKey( final String applicationKey )
    {
        this.applicationKey = applicationKey;
    }
}
