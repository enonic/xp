package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public final class MappingHandler
    implements WebHandler
{
    private final MappingHandlerHelper mappingHandlerHelper;

    @Activate
    public MappingHandler( @Reference final SiteService siteService, @Reference final ResourceService resourceService,
                           @Reference final ControllerScriptFactory controllerScriptFactory,
                           @Reference final FilterScriptFactory filterScriptFactory, @Reference final RendererDelegate rendererDelegate,
                           @Reference final PageTemplateService pageTemplateService,
                           @Reference final PageDescriptorService pageDescriptorService,
                           @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        this.mappingHandlerHelper =
            new MappingHandlerHelper( resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate,
                                      new ControllerMappingsResolver( siteService ),
                                      new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService ) );
    }

    @Override
    public int getOrder()
    {
        return -10;
    }

    @Override
    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        if ( webRequest.getEndpointPath() != null )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        return this.mappingHandlerHelper.handle( webRequest, webResponse, webHandlerChain );
    }
}
