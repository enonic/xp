package com.enonic.xp.portal.impl.url;

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

    BaseUrlMetadata extract( final BaseUrlParams params, final String baseUrl )
    {
        final boolean noExplicitContext = baseUrl == null && params.getProjectName() == null && params.getBranch() == null;

        final ProjectName projectName = ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( noExplicitContext )
            .build()
            .resolve();

        final Branch branch =
            ContentBranchResolver.create().setBranch( params.getBranch() ).setPreferSiteRequest( noExplicitContext ).build().resolve();

        final BaseUrlMetadata.Builder builder = BaseUrlMetadata.create();

        builder.setProjectName( projectName );
        builder.setBranch( branch );

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( noExplicitContext && params.getApi() == null && PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            final StringBuilder str = new StringBuilder( portalRequest.getBaseUri() );

            UrlBuilderHelper.appendSubPath( str, projectName.toString() );
            UrlBuilderHelper.appendSubPath( str, branch.toString() );

            builder.setBaseUrl( str.toString() );
        }
        else
        {
            final Context context =
                ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

            final Content content = context.callWith( () -> resolveContentAnchor( params ) );

            builder.setContent( content );

            Site site = null;
            if ( content instanceof Site )
            {
                site = (Site) content;
            }
            else if ( content != null && !content.getPath().isRoot() )
            {
                site = context.callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );
            }

            if ( site != null )
            {
                builder.setNearestSite( site );
            }

            if ( baseUrl != null )
            {
                builder.setBaseUrl( baseUrl );
            }
            else
            {
                final SiteConfigs siteConfigs;
                if ( site != null )
                {
                    siteConfigs = SiteConfigsDataSerializer.fromData( site.getData().getRoot() );
                }
                else
                {
                    final Project resolvedProject = resolveProject( projectName, portalRequest );
                    siteConfigs = resolvedProject != null ? resolvedProject.getSiteConfigs() : SiteConfigs.empty();
                }

                builder.setSiteConfigs( siteConfigs );
                builder.setBaseUrl( extractBaseUrl( siteConfigs ) );
            }
        }

        return builder.build();
    }

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
        if ( params.getContent() != null )
        {
            return params.getContent().get();
        }

        if ( params.getId() != null )
        {
            return contentService.getById( ContentId.from( params.getId() ) );
        }

        if ( params.getPath() != null )
        {
            final ContentPath path = ContentPath.from( params.getPath() );
            return path.isRoot() ? null : contentService.getByPath( path );
        }

        return null;
    }
}
