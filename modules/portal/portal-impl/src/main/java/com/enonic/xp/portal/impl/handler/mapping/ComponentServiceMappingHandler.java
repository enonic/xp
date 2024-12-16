package com.enonic.xp.portal.impl.handler.mapping;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ComponentServiceMappingHandler
    extends EndpointHandler
{
    private final MappingHandlerHelper mappingHandlerHelper;

    @Activate
    public ComponentServiceMappingHandler( @Reference final ProjectService projectService, @Reference final ResourceService resourceService,
                                           @Reference final ControllerScriptFactory controllerScriptFactory,
                                           @Reference final FilterScriptFactory filterScriptFactory,
                                           @Reference final RendererDelegate rendererDelegate, @Reference final SiteService siteService,
                                           @Reference final ContentService contentService,
                                           @Reference final PageTemplateService pageTemplateService,
                                           @Reference final PageDescriptorService pageDescriptorService,
                                           @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        super( HttpMethod.standard(), "component" );

        this.mappingHandlerHelper =
            new MappingHandlerHelper( projectService, resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate,
                                      new ControllerMappingsResolver( siteService ), new ContentResolver( contentService ),
                                      new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService ));
    }

    @Override
    public int getOrder()
    {
        return -10;
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return super.canHandle( webRequest ) && isSiteBase( webRequest );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        return this.mappingHandlerHelper.handle( webRequest, webResponse, webHandlerChain );
    }


}
