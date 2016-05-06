package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = PortalHandler.class)
public final class MappingWebHandler
    extends BaseWebHandler
{
    private SiteService siteService;

    private ContentService contentService;

    private ResourceService resourceService;

    private ControllerScriptFactory controllerScriptFactory;

    private RendererFactory rendererFactory;

    public MappingWebHandler()
    {
        super( -25 );
    }

    @Override
    public final boolean canHandle( final WebRequest webRequest )
    {
        if ( webRequest instanceof PortalWebRequest )
        {
            final PortalRequest portalRequest = convertToPortalRequest( (PortalWebRequest) webRequest );
            return new ControllerMappingsResolver( siteService, contentService ).resolve( portalRequest ) != null;
        }
        return false;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final PortalRequest portalRequest = convertToPortalRequest( (PortalWebRequest) webRequest );
        final ControllerMappingDescriptor mapping = new ControllerMappingsResolver( siteService, contentService ).resolve( portalRequest );

        return MappingWebHandlerWorker.create().
            webRequest( (PortalWebRequest) webRequest ).
            webResponse( webResponse ).mappingDescriptor( mapping ).
            resourceService( resourceService ).
            controllerScriptFactory( controllerScriptFactory ).
            rendererFactory( rendererFactory ).
            build().
            execute();
    }

    //TODO Temporary fix until renaming of PortalWebRequest to PortalRequest
    @Deprecated
    private PortalRequest convertToPortalRequest( PortalWebRequest portalWebRequest )
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( portalWebRequest.getMethod() );
        portalRequest.getParams().putAll( portalWebRequest.getParams() );
        portalRequest.getHeaders().putAll( portalWebRequest.getHeaders() );
        portalRequest.getCookies().putAll( portalWebRequest.getCookies() );
        portalRequest.setScheme( portalWebRequest.getScheme() );
        portalRequest.setHost( portalWebRequest.getHost() );
        portalRequest.setPort( portalWebRequest.getPort() );
        portalRequest.setPath( portalWebRequest.getPath() );
        portalRequest.setUrl( portalWebRequest.getUrl() );
        portalRequest.setMode( portalWebRequest.getMode() );
        portalRequest.setBranch( portalWebRequest.getBranch() );
        portalRequest.setContentPath( portalWebRequest.getContentPath() );
        portalRequest.setBaseUri( portalWebRequest.getBaseUri() );
        portalRequest.setSite( portalWebRequest.getSite() );
        portalRequest.setContent( portalWebRequest.getContent() );
        portalRequest.setPageTemplate( portalWebRequest.getPageTemplate() );
        portalRequest.setComponent( portalWebRequest.getComponent() );
        portalRequest.setApplicationKey( portalWebRequest.getApplicationKey() );
        portalRequest.setPageDescriptor( portalWebRequest.getPageDescriptor() );
        portalRequest.setControllerScript( portalWebRequest.getControllerScript() );
        portalRequest.setEndpointPath( portalWebRequest.getEndpointPath() );
        portalRequest.setContentType( portalWebRequest.getContentType() );
        portalRequest.setBody( portalWebRequest.getBody() );
        portalRequest.setRawRequest( portalWebRequest.getRawRequest() );
        portalRequest.setWebSocket( portalWebRequest.isWebSocket() );
        return portalRequest;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
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
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Reference
    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}
