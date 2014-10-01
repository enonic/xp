package com.enonic.wem.mustache.internal;

import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.mustache.RenderView;
import com.enonic.wem.script.command.CommandHandler;

public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private final MustacheProcessorFactory mustacheProcessorFactory;

    public RenderViewHandler( final MustacheProcessorFactory mustacheProcessorFactory )
    {
        this.mustacheProcessorFactory = mustacheProcessorFactory;
    }

    @Override
    public Class<RenderView> getType()
    {
        return RenderView.class;
    }

    @Override
    public RenderView newCommand()
    {
        return new RenderView();
    }

    @Override
    public void invoke( final RenderView command )
    {
        final MustacheProcessor processor = this.mustacheProcessorFactory.newProcessor();
        processor.view( command.getView() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }
}
