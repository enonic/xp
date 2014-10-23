package com.enonic.wem.xslt.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.xslt.RenderView;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private XsltProcessorFactory factory;

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
        final XsltProcessor processor = this.factory.newProcessor();
        processor.view( command.getView() );
        processor.inputXml( command.getInputXml() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }

    @Reference
    public void setFactory( final XsltProcessorFactory factory )
    {
        this.factory = factory;
    }
}
