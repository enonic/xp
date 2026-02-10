package com.enonic.xp.portal.impl.handler.mapping;

import java.util.EnumSet;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.handler.HandlerHelper;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ImageServiceMappingHandler
    extends BaseWebHandler
{
    private final MappingHandlerHelper mappingHandlerHelper;

    private static final Set<String> SUPPORTED_ENDPOINTS = Set.of( "image", "attachment" );

    @Activate
    public ImageServiceMappingHandler( @Reference final ResourceService resourceService,
                                       @Reference final ControllerScriptFactory controllerScriptFactory,
                                       @Reference final FilterScriptFactory filterScriptFactory,
                                       @Reference final RendererDelegate rendererDelegate, @Reference final SiteService siteService )
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );

        this.mappingHandlerHelper =
            new MappingHandlerHelper( resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate,
                                      new ControllerMappingsResolver( siteService ), null );
    }

    @Override
    public int getOrder()
    {
        return -10;
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        if ( !PortalRequestHelper.isSiteBase( webRequest ) )
        {
            return false;
        }
        final String endpoint = HandlerHelper.findEndpoint( webRequest );
        return endpoint != null && SUPPORTED_ENDPOINTS.contains( endpoint );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        return this.mappingHandlerHelper.handle( webRequest, webResponse, webHandlerChain );
    }
}
