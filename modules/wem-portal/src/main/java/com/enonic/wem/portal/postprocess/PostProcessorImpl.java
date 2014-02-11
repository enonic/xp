package com.enonic.wem.portal.postprocess;

import javax.inject.Inject;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.rendering.RendererFactory;

public final class PostProcessorImpl
    implements PostProcessor
{
    @Inject
    protected RendererFactory rendererFactory;

    @Override
    public void processResponse( final JsContext context )
    {
        new PostProcessorHandler().context( context ).rendererFactory( this.rendererFactory ).execute();
    }
}
