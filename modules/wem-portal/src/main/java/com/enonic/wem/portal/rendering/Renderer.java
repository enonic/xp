package com.enonic.wem.portal.rendering;

import javax.ws.rs.core.Response;

import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;

public interface Renderer<RENDERABLE extends Renderable>
{
    Response render( RENDERABLE component, JsContext context );
}
