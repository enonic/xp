package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;

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

final class PageNoRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final Supplier<Content> contentSupplier;

    private final String urlType;

    private final ContentService contentService;

    private final ProjectService projectService;

    private PageNoRequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.projectService = Objects.requireNonNull( builder.projectService );
        this.projectNameSupplier = Objects.requireNonNull( builder.projectName );
        this.branchSupplier = Objects.requireNonNull( builder.branch );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.contentSupplier = Objects.requireNonNull( builder.contentSupplier );
    }

    @Override
    public String generateBaseUrl()
    {
        final ProjectName projectName = Objects.requireNonNull( this.projectNameSupplier.get() );
        final Branch branch = Objects.requireNonNull( this.branchSupplier.get() );

        final Content content = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( contentSupplier::get );

        final BaseUrlResult baseUrlResult = BaseUrlResolver.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( content )
            .build()
            .resolve();

        final StringBuilder result = new StringBuilder();

        if ( baseUrlResult.getBaseUrl() == null )
        {
            result.append( "/site/" ).append( projectName ).append( "/" ).append( branch );
            UrlBuilderHelper.appendAndEncodePathParts( result, content.getPath().toString() );
        }
        else
        {
            result.append( normalizeBaseUrl( baseUrlResult.getBaseUrl() ) );
            UrlBuilderHelper.appendAndEncodePathParts( result,
                                                       resolvePath( content.getPath().toString(), baseUrlResult.getNearestSite() ) );
        }

        return result.toString();
    }

    private String resolvePath( final String contentPath, final Site nearestSite )
    {
        if ( nearestSite != null )
        {
            return contentPath.substring( nearestSite.getPath().toString().length() );
        }
        return contentPath;
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

        private Supplier<ProjectName> projectName;

        private Supplier<Branch> branch;

        private Supplier<Content> contentSupplier;

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

        public Builder projectName( final Supplier<ProjectName> projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder branch( final Supplier<Branch> branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder content( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder urlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public PageNoRequestBaseUrlStrategy build()
        {
            return new PageNoRequestBaseUrlStrategy( this );
        }
    }
}
