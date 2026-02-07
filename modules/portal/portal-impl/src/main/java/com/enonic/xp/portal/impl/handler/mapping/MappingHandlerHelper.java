package com.enonic.xp.portal.impl.handler.mapping;

import java.util.Optional;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.handler.HandlerHelper;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.handler.render.PageResolverResult;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

class MappingHandlerHelper
{
    private final ResourceService resourceService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final FilterScriptFactory filterScriptFactory;

    private final RendererDelegate rendererDelegate;

    private final ControllerMappingsResolver controllerMappingsResolver;

    private final PageResolver pageResolver;

    MappingHandlerHelper( final ResourceService resourceService, final ControllerScriptFactory controllerScriptFactory,
                          final FilterScriptFactory filterScriptFactory, final RendererDelegate rendererDelegate,
                          final ControllerMappingsResolver controllerMappingsResolver, final PageResolver pageResolver )
    {
        this.resourceService = resourceService;
        this.controllerScriptFactory = controllerScriptFactory;
        this.filterScriptFactory = filterScriptFactory;
        this.rendererDelegate = rendererDelegate;
        this.controllerMappingsResolver = controllerMappingsResolver;
        this.pageResolver = pageResolver;
    }

    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        if ( !( webRequest instanceof PortalRequest request ) )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        if ( request.getMode() == RenderMode.ADMIN || !PortalRequestHelper.isSiteBase( request ) )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        WebHandlerHelper.checkAdminAccess( request );

        final HttpMethod method = webRequest.getMethod();

        if ( !HttpMethod.isStandard( method ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
        }

        final Site site = request.getSite();

        final SiteConfigs siteConfigs = PortalRequestHelper.getSiteConfigs( request );

        if ( siteConfigs.isEmpty() )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        final Content content = request.getContent();

        final Optional<ControllerMappingDescriptor> optionalControllerMapping =
            controllerMappingsResolver.resolve( PortalRequestHelper.getSiteRelativePath( request ), request.getParams(), content,
                                                siteConfigs, HandlerHelper.findEndpoint( request ) );

        if ( optionalControllerMapping.isPresent() )
        {
            final ControllerMappingDescriptor mapping = optionalControllerMapping.get();

            if ( content == null || pageResolver == null )
            {
                request.setContent( content );
            }
            else
            {
                final PageResolverResult resolvedPage = pageResolver.resolve( content, site != null ? site.getPath() : ContentPath.ROOT );
                final Page effectivePage = resolvedPage.getEffectivePage();
                if ( effectivePage != null )
                {
                    final Content effectiveContent = Content.create( content ).page( effectivePage ).build();
                    request.setContent( effectiveContent );
                    request.setPageDescriptor( resolvedPage.getPageDescriptor() );
                }
                else
                {
                    request.setContent( content );
                }
            }

            request.setContextPath(
                request.getBaseUri() + "/" + RepositoryUtils.getContentRepoName( request.getRepositoryId() ) + "/" + request.getBranch() +
                    ( site != null ? site.getPath() : ContentPath.ROOT ) );
            request.setApplicationKey( mapping.getApplication() );

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
