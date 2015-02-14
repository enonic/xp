package com.enonic.xp.portal.impl.xslt;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private final XsltProcessorFactory factory;

    public RenderViewHandler()
    {
        this.factory = new XsltProcessorFactory();
    }

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

    @Activate
    public void initialize()
    {
        this.factory.initialize();
    }

    @Reference
    public void setUrlService( final PortalUrlService value )
    {
        this.factory.urlService = value;
    }
}
