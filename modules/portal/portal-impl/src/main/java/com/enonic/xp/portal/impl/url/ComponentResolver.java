package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.region.Component;

final class ComponentResolver
{
    private final String component;

    ComponentResolver( String component )
    {
        this.component = component;
    }

    String resolve()
    {
        if ( component != null )
        {
            return component;
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( portalRequest == null )
        {
            return null;
        }

        final Component component = portalRequest.getComponent();
        if ( component == null )
        {
            return null;
        }

        return component.getPath().toString();
    }
}
