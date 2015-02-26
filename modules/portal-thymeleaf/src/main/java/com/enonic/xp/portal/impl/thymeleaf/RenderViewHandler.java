package com.enonic.xp.portal.impl.thymeleaf;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceKey;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler
{
    private final ThymeleafProcessorFactory factory;

    private ViewFunctionService viewFunctionService;

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
        processor.parameter( "portal", createViewFunctions() );
        return processor.process();
    }

    @Reference
    public void setViewFunctionService( final ViewFunctionService value )
    {
        this.viewFunctionService = value;
    }

    private ThymeleafViewFunctions createViewFunctions()
    {
        final ThymeleafViewFunctions functions = new ThymeleafViewFunctions();
        functions.viewFunctionService = this.viewFunctionService;
        functions.context = PortalContextAccessor.get();
        return functions;
    }
}
