package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class PageCustomBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final String baseUrl;

    private final String urlType;

    private final ProjectName projectName;

    private final Branch branch;

    private final Supplier<Content> contentSupplier;

    private final ContentService contentService;

    private PageCustomBaseUrlStrategy( final Builder builder )
    {
        this.baseUrl = Objects.requireNonNull( builder.baseUrl );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.contentSupplier = Objects.requireNonNull( builder.contentSupplier );
        this.contentService = Objects.requireNonNull( builder.contentService );
    }

    @Override
    public String generateBaseUrl()
    {
        final String normalizedBaseUrl = normalizeBaseUrl( baseUrl );

        final Content content = contentSupplier.get();

        if ( content == null )
        {
            throw new NotFoundException( "Content not found" )
            {
            };
        }

        final Site nearestSite = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentService.getNearestSite( content.getId() ) );

        final StringBuilder result = new StringBuilder( normalizedBaseUrl );
        UrlBuilderHelper.appendAndEncodePathParts( result, resolvePath( content.getPath().toString(), nearestSite ) );
        return result.toString();
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

    private String resolvePath( final String contentPath, final Site nearestSite )
    {
        if ( nearestSite != null )
        {
            return contentPath.substring( nearestSite.getPath().toString().length() );
        }
        return contentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String baseUrl;

        private String urlType;

        private ProjectName projectName;

        private Branch branch;

        private Supplier<Content> contentSupplier;

        private ContentService contentService;

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public Builder setProjectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setContentSupplier( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder setContentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public PageCustomBaseUrlStrategy build()
        {
            return new PageCustomBaseUrlStrategy( this );
        }
    }
}
