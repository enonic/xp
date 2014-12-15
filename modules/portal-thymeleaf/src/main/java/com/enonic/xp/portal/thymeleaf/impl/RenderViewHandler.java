package com.enonic.xp.portal.thymeleaf.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private final ThymeleafProcessorFactory factory;

    public RenderViewHandler()
    {
        this.factory = new ThymeleafProcessorFactory();
    }

    @Override
    public String getName()
    {
        return "thymeleaf.render";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final ThymeleafProcessor processor = this.factory.newProcessor();
        processor.view( req.param( "view" ).required().value( ResourceKey.class ) );
        processor.parameters( req.param( "model" ).map() );
        return processor.process();
    }
}
