package com.enonic.xp.portal.impl.jslib.current;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class GetCurrentContentHandler
    implements CommandHandler
{
    @Override
    public String getName()
    {
        return "portal.getContent";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final Content content = portalRequest.getContent();
        return content != null ? convert( content ) : null;
    }

    private Object convert( final Content content )
    {
        return content == null ? null : new ContentMapper( content );
    }

}
