package com.enonic.xp.portal.impl.handler.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.web.handler.EndpointHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class ServiceHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ResourceService resourceService;

    private ServiceDescriptorService serviceDescriptorService;

    private ControllerScriptFactory controllerScriptFactory;

    public ServiceHandler()
    {
        super( "service" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String endpointSubPath = getEndpointSubPath( webRequest );
        final Matcher matcher = PATTERN.matcher( endpointSubPath );
        if ( !matcher.find() )
        {
            throw notFound( "Not a valid service url pattern" );
        }

        final PortalWebRequest portalWebRequest =
            webRequest instanceof PortalWebRequest ? (PortalWebRequest) webRequest : PortalWebRequest.create( webRequest ).build();

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );

        return ServiceWebHandlerWorker.create().
            webRequest( portalWebRequest ).
            webResponse( webResponse ).
            contentService( contentService ).
            resourceService( resourceService ).
            serviceDescriptorService( serviceDescriptorService ).
            controllerScriptFactory( controllerScriptFactory ).
            applicationKey( applicationKey ).
            name( matcher.group( 2 ) ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setServiceDescriptorService( final ServiceDescriptorService serviceDescriptorService )
    {
        this.serviceDescriptorService = serviceDescriptorService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
