package com.enonic.wem.mustache.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.script.command.CommandHandler2;
import com.enonic.wem.script.command.CommandRequest;

@Component(immediate = true)
public final class RenderViewHandler2
    implements CommandHandler2
{
    private MustacheProcessorFactory factory;

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

    @Reference
    public void setFactory( final MustacheProcessorFactory factory )
    {
        this.factory = factory;
    }
}
