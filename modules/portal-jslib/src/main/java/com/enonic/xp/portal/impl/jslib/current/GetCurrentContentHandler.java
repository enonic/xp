package com.enonic.xp.portal.impl.jslib.current;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;

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
        final PortalContext context = PortalContextAccessor.get();
        final Content content = context.getContent();
        return content != null ? convert( content ) : null;
    }

    private Object convert( final Content content )
    {
        return content == null ? null : new ContentMapper( content );
    }

}
