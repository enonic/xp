package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface RendererDelegate
{
    PortalResponse render( Object renderable, PortalRequest portalRequest );
}
