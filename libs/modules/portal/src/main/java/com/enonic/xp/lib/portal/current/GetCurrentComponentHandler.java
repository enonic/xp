package com.enonic.xp.lib.portal.current;

import com.enonic.xp.lib.mapper.ComponentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.region.Component;

public final class GetCurrentComponentHandler
{

    public ComponentMapper execute()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final Component component = portalRequest.getComponent();
        return component != null ? convert( component ) : null;
    }

    private ComponentMapper convert( final Component component )
    {
        return component == null ? null : new ComponentMapper( component );
    }

}
