package com.enonic.wem.portal.postprocess;

import com.google.common.base.Throwables;

public final class PostProcessorFactoryImpl
    implements PostProcessorFactory
{
    public PostProcessorFactoryImpl()
    {
    }

    @Override
    public PostProcessor newPostProcessor()
    {
        final PostProcessorString postProcessor = new PostProcessorString();
        try
        {
            postProcessor.expressionExecutor = new JavaElExpressionExecutor( false );
            return postProcessor;
        }
        catch ( Exception e )
        {
            throw Throwables.propagate( e );
        }
    }
}
