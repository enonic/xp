package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class WidgetHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ControllerScriptFactory controllerScriptFactory;

    public WidgetHandler()
    {
        super( "widgets" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final String restPath = findRestPath( webRequest );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid service url pattern" );
        }

        final ApplicationKey appKey = ApplicationKey.from( matcher.group( 1 ) );
        final ResourceKey scriptDir = ResourceKey.from( appKey, "admin/widgets/" + matcher.group( 2 ) );

        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        final WidgetHandlerWorker worker = new WidgetHandlerWorker( portalRequest );
        worker.scriptDir = scriptDir;
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.setContentService( this.contentService );
        return worker.execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
