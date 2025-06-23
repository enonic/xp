package com.enonic.xp.portal.impl.handler.mapping;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.handler.render.PageResolverResult;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
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
    private static final Pattern PATTERN = Pattern.compile( "^/_/([^/]+)/.*" );

    private final ContentService contentService;

    private final ResourceService resourceService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final FilterScriptFactory filterScriptFactory;

    private final RendererDelegate rendererDelegate;

    private final ControllerMappingsResolver controllerMappingsResolver;

    private final ContentResolver contentResolver;

    private final PageResolver pageResolver;

    MappingHandlerHelper( final ContentService contentService, final ResourceService resourceService,
                          final ControllerScriptFactory controllerScriptFactory, final FilterScriptFactory filterScriptFactory,
                          final RendererDelegate rendererDelegate, final ControllerMappingsResolver controllerMappingsResolver,
                          final ContentResolver contentResolver )
    {
        this( contentService, resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate, controllerMappingsResolver,
              contentResolver, null );
    }

    MappingHandlerHelper( final ContentService contentService, final ResourceService resourceService,
                          final ControllerScriptFactory controllerScriptFactory, final FilterScriptFactory filterScriptFactory,
                          final RendererDelegate rendererDelegate, final ControllerMappingsResolver controllerMappingsResolver,
                          final ContentResolver contentResolver, final PageResolver pageResolver )
    {
        this.contentService = contentService;
        this.resourceService = resourceService;
        this.controllerScriptFactory = controllerScriptFactory;
        this.filterScriptFactory = filterScriptFactory;
        this.rendererDelegate = rendererDelegate;
        this.controllerMappingsResolver = controllerMappingsResolver;
        this.contentResolver = contentResolver;
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

        if ( !HttpMethod.standard().contains( method ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", method ) );
        }

        final ContentResolverResult resolvedContent = contentResolver.resolve( request );

        final Content siteOrProject = resolvedContent.getNearestSite() != null
            ? resolvedContent.getNearestSite()
            : ContextBuilder.from( ContextAccessor.current() )
                .repositoryId( request.getRepositoryId() )
                .branch( request.getBranch() )
                .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() )
                               .principals( RoleKeys.CONTENT_MANAGER_ADMIN )
                               .build() )
                .build()
                .callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

        final SiteConfigs siteConfigs = new SiteConfigsDataSerializer().fromProperties( siteOrProject.getData().getRoot() ).build();

        if ( siteConfigs.isEmpty() )
        {
            return webHandlerChain.handle( webRequest, webResponse );
        }

        final Content content = resolvedContent.getContent();

        final Optional<ControllerMappingDescriptor> optionalControllerMapping =
            controllerMappingsResolver.resolve( resolvedContent.getSiteRelativePath(), request.getParams(), content, siteConfigs,
                                                getServiceType( request ) );

        if ( optionalControllerMapping.isPresent() )
        {
            final ControllerMappingDescriptor mapping = optionalControllerMapping.get();

            if ( content == null || pageResolver == null )
            {
                request.setContent( content );
            }
            else
            {
                final PageResolverResult resolvedPage = pageResolver.resolve( content, siteOrProject.getPath() );
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

            request.setSite( siteOrProject );
            request.setContextPath(
                request.getBaseUri() + "/" + RepositoryUtils.getContentRepoName( request.getRepositoryId() ) + "/" + request.getBranch() +
                    siteOrProject.getPath() );
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

    private String getServiceType( final PortalRequest req )
    {
        final String endpointPath = req.getEndpointPath();

        if ( Strings.isNullOrEmpty( endpointPath ) )
        {
            return null;
        }

        final Matcher matcher = PATTERN.matcher( endpointPath );

        if ( matcher.find() )
        {
            return matcher.group( 1 );
        }

        return null;
    }
}
