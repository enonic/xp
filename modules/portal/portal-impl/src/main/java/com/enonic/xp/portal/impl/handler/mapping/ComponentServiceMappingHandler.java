package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.handler.HandlerHelper;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ComponentServiceMappingHandler
    extends BaseWebHandler
{
    private final MappingHandlerHelper mappingHandlerHelper;

    @Activate
    public ComponentServiceMappingHandler( @Reference final ResourceService resourceService,
                                           @Reference final ControllerScriptFactory controllerScriptFactory,
                                           @Reference final FilterScriptFactory filterScriptFactory,
                                           @Reference final RendererDelegate rendererDelegate, @Reference final SiteService siteService,
                                           @Reference final PageTemplateService pageTemplateService,
                                           @Reference final PageDescriptorService pageDescriptorService,
                                           @Reference final LayoutDescriptorService layoutDescriptorService,
                                           @Reference final SseManager sseManager )
    {
        super( HttpMethod.standard() );

        this.mappingHandlerHelper =
            new MappingHandlerHelper( resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate,
                                      new ControllerMappingsResolver( siteService ),
                                      new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService ), sseManager );
    }

    @Override
    public int getOrder()
    {
        return -10;
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return PortalRequestHelper.isSiteBase( webRequest ) && "component".equals( HandlerHelper.findEndpoint( webRequest ) );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        return this.mappingHandlerHelper.handle( webRequest, webResponse, webHandlerChain );
    }
}
