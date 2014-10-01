package com.enonic.wem.xslt.internal;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.xslt.RenderView;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private final XsltProcessorFactory xsltProcessorFactory;

    public RenderViewHandler( final XsltProcessorFactory xsltProcessorFactory )
    {
        this.xsltProcessorFactory = xsltProcessorFactory;
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
        final XsltProcessor processor = this.xsltProcessorFactory.newProcessor();
        processor.view( command.getView() );
        processor.inputXml( command.getInputXml() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }
}
