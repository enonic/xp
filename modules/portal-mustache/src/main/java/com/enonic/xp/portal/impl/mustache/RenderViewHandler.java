package com.enonic.xp.portal.impl.mustache;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private final MustacheProcessorFactory factory;

    public RenderViewHandler()
    {
        this.factory = new MustacheProcessorFactory();
    }

    @Override
    public String getName()
    {
        return "mustache.render";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final MustacheProcessor processor = this.factory.newProcessor();
        processor.view( req.param( "view" ).required().value( ResourceKey.class ) );
        processor.parameters( req.param( "model" ).map() );
        return processor.process();
    }
}
