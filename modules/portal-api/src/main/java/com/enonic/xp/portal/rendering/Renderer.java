package com.enonic.xp.portal.rendering;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.rendering.Renderable;

@Beta
public interface Renderer<R extends Renderable>
{
    Class<R> getType();

    RenderResult render( R component, PortalContext context );
}
