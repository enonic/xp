package com.enonic.xp.portal.jslib.impl.current;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.portal.jslib.impl.mapper.SiteMapper;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;

@Component(immediate = true)
public final class GetCurrentSiteHandler
    implements CommandHandler
{
    private ContentService contentService;

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
            final PortalContext context = PortalContextAccessor.get();
            final Site site = context.getSite();
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
            return convert( this.contentService.getNearestSite( key ) );
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
}
