package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.lib.mapper.SiteMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteHandler
{
    private final ContentService contentService;

    private String key;

    public GetCurrentSiteHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public SiteMapper execute()
    {
        final String key = this.key;
        if ( key == null )
        {
            final PortalRequest portalRequest = PortalRequestAccessor.get();
            final Site site = portalRequest.getSite();
            return site != null ? convert( site ) : null;
        }
        else if ( key.startsWith( "/" ) )
        {
            return getByPath( ContentPath.from( key ) );
        }
        else
        {
            return getById( ContentId.from( key ) );
        }
    }

    private SiteMapper getByPath( final ContentPath key )
    {
        try
        {
            final Content content = this.contentService.getByPath( key );
            return content != null ? getById( content.getId() ) : null;
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private SiteMapper getById( final ContentId key )
    {
        try
        {
            return convert( this.contentService.getNearestSite( key ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private SiteMapper convert( final Site site )
    {
        return site == null ? null : new SiteMapper( site );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

}
