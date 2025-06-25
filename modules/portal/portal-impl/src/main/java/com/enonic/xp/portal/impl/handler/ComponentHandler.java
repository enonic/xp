package com.enonic.xp.portal.impl.handler;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.handler.render.PageResolver;
import com.enonic.xp.portal.impl.handler.render.PageResolverResult;
import com.enonic.xp.portal.impl.rendering.FragmentPageResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(service = ComponentHandler.class)
public class ComponentHandler
{
    private final RendererDelegate rendererDelegate;

    private final PageTemplateService pageTemplateService;

    private final PostProcessor postProcessor;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final PageDescriptorService pageDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    @Activate
    public ComponentHandler( @Reference final ContentService contentService, @Reference final RendererDelegate rendererDelegate,
                             @Reference final PageTemplateService pageTemplateService, @Reference final PostProcessor postProcessor,
                             @Reference final PageDescriptorService pageDescriptorService,
                             @Reference final LayoutDescriptorService layoutDescriptorService, @Reference final ProjectService projectService )
    {
        this.contentService = contentService;
        this.rendererDelegate = rendererDelegate;
        this.pageTemplateService = pageTemplateService;
        this.postProcessor = postProcessor;
        this.pageDescriptorService = pageDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.projectService = projectService;
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        if ( !PortalRequestHelper.isSiteBase( webRequest ) )
        {
            throw WebException.notFound( "Not a valid request" );
        }

        if ( !HttpMethod.standard().contains( webRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        WebHandlerHelper.checkAdminAccess( webRequest );

        final PortalRequest portalRequest = updatePortalRequest( webRequest );

        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return doHandle( portalRequest );
        }

        trace.put( "componentPath", portalRequest.getComponent().getPath() );
        trace.put( "type", portalRequest.getComponent().getType().toString() );

        return Tracer.traceEx( trace, () -> doHandle( portalRequest ) );
    }

    private PortalResponse doHandle( final PortalRequest portalRequest )
    {
        final PortalResponse result = rendererDelegate.render( portalRequest.getComponent(), portalRequest );
        final boolean isEditMode = portalRequest.getMode() == RenderMode.EDIT;
        final PortalResponse response = ( isEditMode && result.hasContributions() ) ? addHasContributionsHeader( result ) : result;

        return this.postProcessor.processResponseInstructions( portalRequest, response );
    }

    private PortalRequest updatePortalRequest( final WebRequest webRequest )
    {
        final PortalRequest portalRequest = (PortalRequest) webRequest;

        final ContentResolver contentResolver = new ContentResolver( contentService, projectService );
        final ContentResolverResult resolvedContent = contentResolver.resolve( portalRequest );

        final Content content = resolvedContent.getContentOrElseThrow();
        final Site site = resolvedContent.getNearestSiteOrElseThrow();

        final PageResolver pageResolver = new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService );
        final PageResolverResult resolvedPage = pageResolver.resolve( content, site.getPath() );
        Page effectivePage = resolvedPage.getEffectivePageOrElseThrow( portalRequest.getMode() );
        com.enonic.xp.region.Component component = null;
        final ComponentPath componentPath = ComponentPath.from( HandlerHelper.findRestPath( portalRequest, "component" ) );

        if ( content.getType().isFragment() )
        {
            // fragment content, try resolving component path in Layout fragment
            final com.enonic.xp.region.Component fragmentComponent = effectivePage.getFragment();

            if ( componentPath.isEmpty() )
            {
                component = fragmentComponent;
            }
            else if ( fragmentComponent instanceof LayoutComponent )
            {
                component = ( (LayoutComponent) fragmentComponent ).getComponent( componentPath );
            }
        }

        if ( component == null )
        {
            effectivePage = inlineFragments( effectivePage, componentPath );
            component = effectivePage.getRegions().getComponent( componentPath );
        }

        if ( component == null )
        {
            throw WebException.notFound( String.format( "Page component for [%s] not found", componentPath ) );
        }

        final Content effectiveContent = Content.create( content ).page( effectivePage ).build();

        portalRequest.setSite( site );
        portalRequest.setProject( resolvedContent.getProject() );
        portalRequest.setContent( effectiveContent );
        portalRequest.setComponent( component );
        portalRequest.setApplicationKey( resolvedPage.getApplicationKey() );

        return portalRequest;
    }

    private Page inlineFragments( Page page, final ComponentPath componentPath )
    {
        // traverse page based on componentPath, inline fragments components if found
        final List<ComponentPath.RegionAndComponent> partialComponentPathParts = new ArrayList<>();

        Page resolvedPage = page;
        for ( ComponentPath.RegionAndComponent pathPart : componentPath )
        {
            partialComponentPathParts.add( pathPart );
            final ComponentPath path = new ComponentPath( partialComponentPathParts );
            final com.enonic.xp.region.Component component = page.getRegions().getComponent( path );

            if ( component == null )
            {
                break;
            }

            if ( component instanceof FragmentComponent )
            {
                final FragmentComponent fragment = (FragmentComponent) component;
                final com.enonic.xp.region.Component fragmentComponent = getFragmentComponent( fragment );
                if ( fragmentComponent == null )
                {
                    break;
                }
                resolvedPage = new FragmentPageResolver().inlineFragmentInPage( page, fragmentComponent, path );
            }
        }
        return resolvedPage;
    }

    private com.enonic.xp.region.Component getFragmentComponent( final FragmentComponent component )
    {
        final ContentId contentId = component.getFragment();
        if ( contentId == null )
        {
            return null;
        }
        try
        {
            final Content fragmentContent = contentService.getById( contentId );
            if ( fragmentContent.getPage() == null || !fragmentContent.getType().isFragment() )
            {
                return null;
            }
            return fragmentContent.getPage().getFragment();
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private PortalResponse addHasContributionsHeader( final PortalResponse source )
    {
        return PortalResponse.create( source ).header( "X-Has-Contributions", "true" ).build();
    }
}
