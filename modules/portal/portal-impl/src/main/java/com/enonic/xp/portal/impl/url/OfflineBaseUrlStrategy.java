package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

final class OfflineBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ProjectName projectName;

    private final Branch branch;

    private final Content content;

    private final String urlType;

    private final ContentService contentService;

    private final ProjectService projectService;

    OfflineBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.projectService = Objects.requireNonNull( builder.projectService );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.content = builder.content;
    }

    @Override
    public String generateBaseUrl()
    {
        if ( content == null )
        {
            return "/api";
        }

        Site site;
        if ( !( content instanceof Site ) )
        {
            site = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( projectName.getRepoId() )
                .branch( branch )
                .build()
                .callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );
        }
        else
        {
            site = (Site) content;
        }

        if ( site != null )
        {
            String siteBaseUrl = resolveBaseUrl( site.getSiteConfigs() );
            if ( siteBaseUrl != null )
            {
                return normalizeBaseUrl( siteBaseUrl );
            }
        }

        Project project = projectService.get( projectName );
        if ( project != null )
        {
            String projectBaseUrl = resolveBaseUrl( project.getSiteConfigs() );
            if ( projectBaseUrl != null )
            {
                return normalizeBaseUrl( projectBaseUrl );
            }
        }

        return "/api";
    }

    public static Builder create()
    {
        return new Builder();
    }

    private String resolveBaseUrl( final SiteConfigs siteConfigs )
    {
        final SiteConfig siteConfig = siteConfigs.get( ApplicationKey.from( "com.enonic.xp.site" ) );
        if ( siteConfig != null )
        {
            return siteConfig.getConfig().getString( "baseUrl" );
        }
        return null;
    }

    private String normalizeBaseUrl( final String baseUrl )
    {
        final String path = UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) ? URI.create( baseUrl ).getPath() : baseUrl;
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 ) + "/_/";
        }
        return path + "/_/";
    }

    static class Builder
    {
        private ContentService contentService;

        private ProjectService projectService;

        private ProjectName projectName;

        private Branch branch;

        private Content content;

        private String urlType;

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder projectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        public Builder projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder content( final Content content )
        {
            this.content = content;
            return this;
        }

        public Builder urlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public OfflineBaseUrlStrategy build()
        {
            return new OfflineBaseUrlStrategy( this );
        }
    }
}
