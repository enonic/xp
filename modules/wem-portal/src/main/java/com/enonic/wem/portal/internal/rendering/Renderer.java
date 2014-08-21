package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.internal.controller.JsContext;

public interface Renderer<RENDERABLE extends Renderable>
{
    RenderResult render( RENDERABLE component, JsContext context );
}
