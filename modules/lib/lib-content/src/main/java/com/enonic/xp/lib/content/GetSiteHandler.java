package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.lib.content.mapper.SiteMapper;
import com.enonic.xp.site.Site;

public final class GetSiteHandler
    extends BaseContextHandler
{
    private String key;

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

    private SiteMapper getByPath( final ContentPath contentPath )
    {
        try
        {
            final Content content = this.contentService.getByPath( contentPath );
            return getById( content.getId() );
        }
        catch ( final ContentNotFoundException e )
        {
        }
        return null;
    }

    private SiteMapper getById( final ContentId contentId )
    {
        try
        {
            final Site site = this.contentService.getNearestSite( contentId );
            if ( site != null )
            {
                return convert( site );
            }
        }
        catch ( final ContentNotFoundException e )
        {
        }
        return null;
    }

    private SiteMapper convert( final Site content )
    {
        return new SiteMapper( content );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }
}
