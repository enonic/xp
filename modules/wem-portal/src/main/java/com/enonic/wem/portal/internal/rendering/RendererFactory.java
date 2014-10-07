package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.PortalContext;

public interface RendererFactory
{
    <T extends Renderable, CONTEXT extends PortalContext> Renderer<T, CONTEXT> getRenderer( T renderable );
}
