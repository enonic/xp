package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = PortalHandler.class)
public final class WidgetWebHandler
    extends BaseWebHandler
{
    final static String ADMIN_WIDGET_PREFIX = "/admin/widget/";

    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private ControllerScriptFactory controllerScriptFactory;

    public WidgetWebHandler()
    {
        super( 50 );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getPath().startsWith( ADMIN_WIDGET_PREFIX );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String subPath = webRequest.getPath().substring( ADMIN_WIDGET_PREFIX.length() );
        final Matcher matcher = PATTERN.matcher( subPath );
        if ( !matcher.find() )
        {
            throw notFound( "Not a valid service url pattern" );
        }

        final ApplicationKey appKey = ApplicationKey.from( matcher.group( 1 ) );
        final ResourceKey scriptDir = ResourceKey.from( appKey, "admin/widgets/" + matcher.group( 2 ) );

        return WidgetWebHandlerWorker.create().
            webRequest( webRequest ).
            webResponse( webResponse ).
            scriptDir( scriptDir ).
            controllerScriptFactory( controllerScriptFactory ).
            build().
            execute();
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
