package com.enonic.xp.portal.impl.url;

import java.util.concurrent.Callable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static java.util.Objects.requireNonNull;

record BaseUrlExtractor(ContentService contentService, ProjectService projectService)
{
    BaseUrlExtractor( final ContentService contentService, final ProjectService projectService )
    {
        this.contentService = requireNonNull( contentService );
        this.projectService = requireNonNull( projectService );
    }

    BaseUrlMetadata extract( final BaseUrlParams params, final boolean followRequest )
    {
        return extract( params.getProjectName(), params.getBranch(), () -> resolveContentAnchor( params ), followRequest, true );
    }

    /**
     * Resolves from configuration alone: the project and branch come from the params or the
     * current context, the configuration of a project is read from the project service, and the
     * Base URL is the one configured, {@code null} when there is none.
     */
    BaseUrlMetadata extractFromConfiguration( final String baseKey, final String projectName, final String branch )
    {
        return extract( projectName, branch, () -> resolveContent( baseKey ), false, false );
    }

    /**
     * @param anchor the content the URL is anchored to, looked up in the resolved project and branch
     */
    private BaseUrlMetadata extract( final String explicitProjectName, final String explicitBranch,
                                     final Callable<Content> anchor, final boolean followRequest, final boolean contextFromRequest )
    {
        final boolean noExplicitContext = contextFromRequest && explicitProjectName == null && explicitBranch == null;

        final ProjectName projectName = ContentProjectResolver.create()
            .setProjectName( explicitProjectName )
            .setPreferSiteRequest( noExplicitContext )
            .build()
            .resolve();

        final Branch branch =
            ContentBranchResolver.create().setBranch( explicitBranch ).setPreferSiteRequest( noExplicitContext ).build().resolve();

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( followRequest && noExplicitContext && PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            final StringBuilder str = new StringBuilder( portalRequest.getBaseUri() );

            UrlBuilderHelper.appendSubPath( str, projectName.toString() );
            UrlBuilderHelper.appendSubPath( str, branch.toString() );

            return new BaseUrlMetadata( projectName, branch, str.toString(), null, ContentPath.ROOT, SiteConfigs.empty() );
        }

        final Context context =
            ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

        final Content content = context.callWith( anchor );

        // the base URL belongs to the site of this content, and the URL is anchored there.
        // Configuration of a site is never inherited from a parent site, so that site decides
        // on its own which Base URL applies
        final Site site = context.callWith( () -> resolveSite( content ) );

        final SiteConfigs siteConfigs;
        if ( site != null )
        {
            siteConfigs = SiteConfigsDataSerializer.fromData( site.getData().getRoot() );
        }
        else
        {
            final Project resolvedProject =
                contextFromRequest ? resolveProject( projectName, portalRequest ) : projectService.get( projectName );
            siteConfigs = resolvedProject != null ? resolvedProject.getSiteConfigs() : SiteConfigs.empty();
        }

        final ContentPath anchorPath = site != null ? site.getPath() : ContentPath.ROOT;

        return new BaseUrlMetadata( projectName, branch, extractBaseUrl( siteConfigs ), content, anchorPath, siteConfigs );
    }

    private Site resolveSite( final Content content )
    {
        if ( content instanceof Site )
        {
            return (Site) content;
        }
        if ( content != null && !content.getPath().isRoot() )
        {
            return contentService.getNearestSite( ContentId.from( content.getId() ) );
        }
        return null;
    }

    /**
     * @return the project, reusing the one on the request when it is the same
     */
    private Project resolveProject( final ProjectName projectName, final PortalRequest portalRequest )
    {
        if ( portalRequest != null )
        {
            final Project current = portalRequest.getProject();
            if ( current != null && projectName.equals( current.getName() ) )
            {
                return current;
            }
        }
        return projectService.get( projectName );
    }

    private String extractBaseUrl( final SiteConfigs siteConfigs )
    {
        final SiteConfig siteConfig = siteConfigs.get( ApplicationKey.from( "portal" ) );
        if ( siteConfig != null )
        {
            return siteConfig.getConfig().getString( "baseUrl" );
        }
        return null;
    }

    /**
     * @return the content the URL is anchored to, or {@code null} when it is anchored at the
     * project itself: the root of a project holds no content, so the URL is then resolved from
     * the configuration of the project rather than of a site
     */
    private Content resolveContentAnchor( final BaseUrlParams params )
    {
        if ( params.getId() != null )
        {
            return contentService.getById( ContentId.from( params.getId() ) );
        }

        if ( params.getPath() != null )
        {
            return resolveContent( params.getPath() );
        }

        return null;
    }

    /**
     * @return the content the key denotes, or {@code null} when it denotes the root of the project
     */
    Content resolveContent( final String contentKey )
    {
        if ( contentKey.startsWith( "/" ) )
        {
            final ContentPath path = ContentPath.from( contentKey );
            return path.isRoot() ? null : contentService.getByPath( path );
        }
        return contentService.getById( ContentId.from( contentKey ) );
    }
}
