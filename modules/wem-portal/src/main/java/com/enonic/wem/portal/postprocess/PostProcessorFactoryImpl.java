package com.enonic.wem.portal.postprocess;

import javax.inject.Inject;

import com.enonic.wem.portal.rendering.RendererFactory;

public final class PostProcessorFactoryImpl
    implements PostProcessorFactory
{
    @Inject
    protected RendererFactory rendererFactory;

    public PostProcessorFactoryImpl()
    {
    }

    @Override
    public PostProcessor newPostProcessor()
    {
        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.setRendererFactory( this.rendererFactory );
        return postProcessor;
    }
}
