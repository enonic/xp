package com.enonic.wem.xslt.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandHandler2;
import com.enonic.wem.script.command.CommandRequest;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

@Component(immediate = true)
public final class RenderViewHandler2
    implements CommandHandler2
{
    private XsltProcessorFactory factory;

    @Override
    public String getName()
    {
        return "xslt.render";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final XsltProcessor processor = this.factory.newProcessor();
        processor.view( req.param( "view" ).required().value( ResourceKey.class ) );
        processor.inputSource( MapToXmlConverter.toSource( req.param( "model" ).map() ) );
        return processor.process();
    }

    @Reference
    public void setFactory( final XsltProcessorFactory factory )
    {
        this.factory = factory;
    }
}
