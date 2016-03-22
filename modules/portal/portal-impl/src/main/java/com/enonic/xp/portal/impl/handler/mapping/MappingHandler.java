package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.BaseHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

@Component(immediate = true, service = PortalHandler.class)
public final class MappingHandler
    extends BaseHandler
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
    public final boolean canHandle( final PortalRequest req )
    {
        return new ControllerMappingsResolver( siteService, contentService ).resolve( req ) != null;
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final ControllerMappingDescriptor mapping = new ControllerMappingsResolver( siteService, contentService ).resolve( req );

        final MappingHandlerWorker worker = new MappingHandlerWorker();
        worker.mappingDescriptor = mapping;
        worker.resourceService = this.resourceService;
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.rendererFactory = rendererFactory;
        return worker;
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
