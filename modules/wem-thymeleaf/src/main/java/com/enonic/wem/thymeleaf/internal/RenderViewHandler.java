package com.enonic.wem.thymeleaf.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private ThymeleafProcessorFactory factory;

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

    @Reference
    public void setFactory( final ThymeleafProcessorFactory factory )
    {
        this.factory = factory;
    }
}
