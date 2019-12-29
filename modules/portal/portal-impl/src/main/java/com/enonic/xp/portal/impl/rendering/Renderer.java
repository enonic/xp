package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface Renderer<R>
{
    Class<R> getType();

    PortalResponse render( R component, PortalRequest portalRequest );
}
