package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.Optional;

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

record BaseUrlExtractor(ContentService contentService, ProjectService projectService)
{
    BaseUrlExtractor( final ContentService contentService, final ProjectService projectService )
    {
        this.contentService = Objects.requireNonNull( contentService );
        this.projectService = Objects.requireNonNull( projectService );
    }

    BaseUrlMetadata extract( final BaseUrlParams params )
    {
        final boolean noExplicitContext = params.getProjectName() == null && params.getBranch() == null;

        final ProjectName projectName = ContentProjectResolver.create()
            .setProjectName( params.getProjectName() )
            .setPreferSiteRequest( noExplicitContext )
            .build()
            .resolve();

        final Branch branch = ContentBranchResolver.create()
            .setBranch( params.getBranch() )
            .setPreferSiteRequest( noExplicitContext )
            .build()
            .resolve();

        final BaseUrlMetadata.Builder builder = BaseUrlMetadata.create();

        builder.setProjectName( projectName );
        builder.setBranch( branch );

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( PortalRequestHelper.isSiteBase( portalRequest ) && noExplicitContext )
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

            final Content content = context.callWith( () -> getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );

            builder.setContent( content );

            Site site = null;
            if ( content instanceof Site )
            {
                site = (Site) content;
            }
            else if ( !content.getPath().isRoot() )
            {
                site = context.callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );
            }

            final SiteConfigs siteConfigs = site != null
                ? site.getSiteConfigs()
                : Optional.ofNullable( projectService.get( projectName ) ).map( Project::getSiteConfigs ).orElse( SiteConfigs.empty() );

            if ( site != null )
            {
                builder.setNearestSite( site );
            }

            builder.setBaseUrl( extractBaseUrl( siteConfigs ) );
        }

        return builder.build();
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

    private Content getContent( final String contentKey )
    {
        if ( contentKey.startsWith( "/" ) )
        {
            return contentService.getByPath( ContentPath.from( contentKey ) );
        }
        else
        {
            return contentService.getById( ContentId.from( contentKey ) );
        }
    }
}
