package com.enonic.xp.portal.impl.handler.mapping;

import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class MappingHandler
    implements WebHandler
{
    private final ResourceService resourceService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final FilterScriptFactory filterScriptFactory;

    private final RendererDelegate rendererDelegate;

    private final ControllerMappingsResolver controllerMappingsResolver;

    private final ContentResolver contentResolver;

    @Activate
    public MappingHandler( @Reference final SiteService siteService, @Reference final ContentResolver contentResolver,
                           @Reference final ResourceService resourceService,
                           @Reference final ControllerScriptFactory controllerScriptFactory,
                           @Reference final FilterScriptFactory filterScriptFactory, @Reference final RendererDelegate rendererDelegate )
    {
        this.resourceService = resourceService;
        this.controllerScriptFactory = controllerScriptFactory;
        this.filterScriptFactory = filterScriptFactory;
        this.rendererDelegate = rendererDelegate;
        this.controllerMappingsResolver = new ControllerMappingsResolver( siteService );
        this.contentResolver = contentResolver;
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
        if ( !( webRequest instanceof PortalRequest ) || webRequest.getEndpointPath() != null )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        final PortalRequest request = (PortalRequest) webRequest;

        if ( request.getContentPath() == null || request.getMode() == RenderMode.ADMIN || !request.isSiteBase() )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        WebHandlerHelper.checkAdminAccess( request );

        final HttpMethod method = webRequest.getMethod();

        if ( !HttpMethod.standard().contains( method ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
        }

        final ContentResolverResult resolvedContent = contentResolver.resolve( request );

        if ( resolvedContent == null )
        {
            return webHandlerChain.handle( request, webResponse );
        }

        final Optional<ControllerMappingDescriptor> resolve =
            controllerMappingsResolver.resolve( resolvedContent.siteRelativePath, request.getParams(), resolvedContent.content,
                                                resolvedContent.nearestSite.getSiteConfigs() );

        if ( resolve.isPresent() )
        {
            final ControllerMappingDescriptor mapping = resolve.get();

            request.setContent( resolvedContent.content );
            request.setSite( resolvedContent.nearestSite );
            request.setContextPath( request.getBaseUri() + "/" + request.getBranch() + resolvedContent.nearestSite.getPath() );

            if ( mapping.isController() )
            {
                return handleController( request, mapping );
            }
            else
            {
                return handleFilter( request, webResponse, webHandlerChain, mapping );
            }
        }
        else
        {
            return webHandlerChain.handle( request, webResponse );
        }
    }

    private PortalResponse handleController( final PortalRequest portalRequest, final ControllerMappingDescriptor mapping )
        throws Exception
    {
        final MappingHandlerWorker worker = new MappingHandlerWorker( portalRequest );
        worker.mappingDescriptor = mapping;
        worker.resourceService = this.resourceService;
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.rendererDelegate = rendererDelegate;
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return worker.execute();
        }
        return Tracer.traceEx( trace, worker::execute );
    }

    private PortalResponse handleFilter( final PortalRequest request, final WebResponse response, final WebHandlerChain webHandlerChain,
                                         final ControllerMappingDescriptor mapping )
        throws Exception
    {
        final MappingFilterHandlerWorker worker = new MappingFilterHandlerWorker( request, response, webHandlerChain );
        worker.mappingDescriptor = mapping;
        worker.resourceService = this.resourceService;
        worker.filterScriptFactory = this.filterScriptFactory;
        final Trace trace = Tracer.newTrace( "filter" );
        if ( trace == null )
        {
            return worker.execute();
        }
        return Tracer.traceEx( trace, worker::execute );
    }
}
