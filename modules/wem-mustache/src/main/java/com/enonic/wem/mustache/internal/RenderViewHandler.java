package com.enonic.wem.mustache.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.mustache.RenderView;
import com.enonic.wem.script.command.CommandHandler;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private MustacheProcessorFactory factory;

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
        final MustacheProcessor processor = this.factory.newProcessor();
        processor.view( command.getView() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }

    @Reference
    public void setFactory( final MustacheProcessorFactory factory )
    {
        this.factory = factory;
    }
}
