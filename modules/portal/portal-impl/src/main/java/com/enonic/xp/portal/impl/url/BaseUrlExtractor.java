package com.enonic.xp.portal.impl.url;

import java.util.Objects;

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
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

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

        final Branch branch =
            ContentBranchResolver.create().setBranch( params.getBranch() ).setPreferSiteRequest( noExplicitContext ).build().resolve();

        final BaseUrlMetadata.Builder builder = BaseUrlMetadata.create();

        builder.setProjectName( projectName );
        builder.setBranch( branch );

        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( PortalRequestHelper.isSiteBase( portalRequest ) && noExplicitContext )
        {
            final StringBuilder str = new StringBuilder( portalRequest.getBaseUri() );

            UrlBuilderHelper.appendSubPath( str, projectName.toString() );
            UrlBuilderHelper.appendSubPath( str, branch.toString() );

            final Content site = portalRequest.getSite();
            if ( site != null && !site.getPath().isRoot() )
            {
                builder.setNearestSite( (Site) site );
            }
            builder.setBaseUrl( str.toString() );
        }
        else
        {
            final Context context =
                ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

            final Content content = context.callWith( () -> getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );

            builder.setContent( content );

            Content siteOrProject = null;
            if ( content instanceof Site )
            {
                siteOrProject = content;
                builder.setNearestSite( (Site) siteOrProject );
            }
            else if ( !content.getPath().isRoot() )
            {
                siteOrProject = context.callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );
            }

            if ( siteOrProject != null )
            {
                builder.setNearestSite( (Site) siteOrProject );
            }
            else
            {
                siteOrProject = context.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );
            }

            final SiteConfigs siteConfigs = siteOrProject != null
                ? new SiteConfigsDataSerializer().fromProperties( siteOrProject.getData().getRoot() ).build()
                : SiteConfigs.empty();

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
