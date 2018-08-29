package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class MappingHandler
    extends BaseWebHandler
{
    private SiteService siteService;

    private ContentService contentService;

    private ResourceService resourceService;

    private ControllerScriptFactory controllerScriptFactory;

    private RendererFactory rendererFactory;

    public MappingHandler()
    {
        super( -10 );
    }

    @Override
    public final boolean canHandle( final WebRequest req )
    {
        if ( !( req instanceof PortalRequest ) )
        {
            return false;
        }
        PortalRequest portalRequest = (PortalRequest) req;
        return portalRequest.isPortalBase() && new ControllerMappingsResolver( siteService, contentService ).canHandle( portalRequest );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        PortalRequest portalRequest = (PortalRequest) webRequest;
        final ControllerMappingDescriptor mapping = new ControllerMappingsResolver( siteService, contentService ).resolve( portalRequest );

        final MappingHandlerWorker worker = new MappingHandlerWorker( portalRequest );
        worker.mappingDescriptor = mapping;
        worker.resourceService = this.resourceService;
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.rendererFactory = rendererFactory;
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return worker.execute();
        }
        return Tracer.traceEx( trace, worker::execute );
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
