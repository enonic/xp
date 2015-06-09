package com.enonic.xp.portal.impl.jslib.current;

import com.enonic.xp.page.region.Component;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.jslib.mapper.ComponentMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@org.osgi.service.component.annotations.Component(immediate = true)
public final class GetCurrentComponentHandler
    implements CommandHandler
{
    @Override
    public String getName()
    {
        return "portal.getComponent";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final Component component = portalRequest.getComponent();
        return component != null ? convert( component ) : null;
    }

    private Object convert( final Component component )
    {
        return component == null ? null : new ComponentMapper( component );
    }

}
