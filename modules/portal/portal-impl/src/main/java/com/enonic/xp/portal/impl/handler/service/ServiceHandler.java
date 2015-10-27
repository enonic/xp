package com.enonic.xp.portal.impl.handler.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.ResourceKey;

@Component(immediate = true, service = PortalHandler.class)
public final class ServiceHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ControllerScriptFactory controllerScriptFactory;

    public ServiceHandler()
    {
        super( "service" );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid service url pattern" );
        }

        final ApplicationKey appKey = ApplicationKey.from( matcher.group( 1 ) );
        final ResourceKey scriptDir = ResourceKey.from( appKey, "site/services/" + matcher.group( 2 ) );

        final ServiceHandlerWorker worker = new ServiceHandlerWorker();
        worker.scriptDir = scriptDir;
        worker.setContentService( this.contentService );
        worker.controllerScriptFactory = this.controllerScriptFactory;
        return worker;
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
