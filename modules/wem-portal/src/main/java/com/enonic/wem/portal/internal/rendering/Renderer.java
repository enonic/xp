package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.PortalContext;

public interface Renderer<RENDERABLE extends Renderable>
{
    Class<RENDERABLE> getType();

    RenderResult render( RENDERABLE component, PortalContext context );
}
