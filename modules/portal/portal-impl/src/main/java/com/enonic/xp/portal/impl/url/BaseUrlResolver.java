package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

final class BaseUrlResolver
{
    private final ProjectName projectName;

    private final Branch branch;

    private final Content content;

    private final ContentService contentService;

    private final ProjectService projectService;

    private BaseUrlResolver( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.projectService = Objects.requireNonNull( builder.projectService );

        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.content = Objects.requireNonNull( builder.content );
    }

    public BaseUrlResult resolve()
    {
        Site site = null;
        if ( content.isSite() )
        {
            site = (Site) content;
        }
        else if ( !content.getPath().isRoot() )
        {
            site = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( projectName.getRepoId() )
                .branch( branch )
                .build()
                .callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );
        }

        final BaseUrlResult.Builder builder = BaseUrlResult.create();

        if ( site != null )
        {
            String siteBaseUrl = resolveBaseUrl( site.getSiteConfigs() );
            if ( siteBaseUrl != null )
            {
                builder.setNearestSite( site );
                builder.setBaseUrl( siteBaseUrl );

                return builder.build();
            }
        }

        Project project = projectService.get( projectName );
        if ( project != null )
        {
            builder.setBaseUrl( resolveBaseUrl( project.getSiteConfigs() ) );
            return builder.build();
        }

        return builder.build();
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

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ContentService contentService;

        private ProjectService projectService;

        private ProjectName projectName;

        private Branch branch;

        private Content content;

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

        public BaseUrlResolver build()
        {
            return new BaseUrlResolver( this );
        }
    }
}
