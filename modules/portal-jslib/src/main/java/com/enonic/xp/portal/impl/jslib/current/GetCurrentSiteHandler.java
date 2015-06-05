package com.enonic.xp.portal.impl.jslib.current;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.jslib.mapper.SiteMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.site.SiteService;

@Component(immediate = true)
public final class GetCurrentSiteHandler
    implements CommandHandler
{
    private ContentService contentService;

    private SiteService siteService;

    @Override
    public String getName()
    {
        return "portal.getSite";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).value( String.class );
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

    private Object getByPath( final ContentPath key )
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

    private Object getById( final ContentId key )
    {
        try
        {
            return convert( this.siteService.getNearestSite( key ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private Object convert( final Site site )
    {
        return site == null ? null : new SiteMapper( site );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
