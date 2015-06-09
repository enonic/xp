package com.enonic.xp.portal.rendering;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

@Beta
public interface Renderer<R>
{
    Class<R> getType();

    PortalResponse render( R component, PortalRequest portalRequest );
}
