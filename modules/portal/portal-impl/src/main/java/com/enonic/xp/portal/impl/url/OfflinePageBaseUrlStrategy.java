package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

final class OfflinePageBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ProjectName projectName;

    private final Branch branch;

    private final Content content;

    private final String urlType;

    private final ContentService contentService;

    private final ProjectService projectService;

    private OfflinePageBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.projectService = Objects.requireNonNull( builder.projectService );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.content = Objects.requireNonNull( builder.content );
    }

    @Override
    public String generateBaseUrl()
    {
        final String baseUrl = BaseUrlResolver.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( content )
            .build()
            .resolve();

        final String normalizedBaseUrl = normalizeBaseUrl( Objects.requireNonNullElse( baseUrl, "/api" ) );

        final Site nearestSite = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentService.getNearestSite( content.getId() ) );

        if ( nearestSite != null )
        {
            return normalizedBaseUrl + content.getPath().toString().replace( nearestSite.getPath().toString(), "" );
        }

        return normalizedBaseUrl + content.getPath();
    }

    private String normalizeBaseUrl( final String baseUrl )
    {
        final String path = UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) ? URI.create( baseUrl ).getPath() : baseUrl;
        if ( path.endsWith( "/" ) )
        {
            return path.substring( 0, path.length() - 1 );
        }
        return path;
    }

    static Builder create()
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

        public OfflinePageBaseUrlStrategy build()
        {
            return new OfflinePageBaseUrlStrategy( this );
        }
    }
}
