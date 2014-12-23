package com.enonic.xp.portal.jslib.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;
import com.enonic.wem.script.mapper.ContentMapper;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;

@Component(immediate = true)
public final class GetContentHandler
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
