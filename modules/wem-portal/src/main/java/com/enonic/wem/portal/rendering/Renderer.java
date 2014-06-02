package com.enonic.wem.portal.rendering;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;

public interface Renderer<RENDERABLE extends Renderable>
{
    RenderResult render( RENDERABLE component, JsContext context );
}
