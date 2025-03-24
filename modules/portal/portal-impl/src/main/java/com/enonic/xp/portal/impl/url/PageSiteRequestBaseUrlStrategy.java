package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;

final class PageSiteRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String id;

    private final String path;

    private final String urlType;

    private final ProjectName projectName;

    private final Branch branch;

    private PageSiteRequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.id = builder.id;
        this.path = builder.path;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
    }

    @Override
    public String generateBaseUrl()
    {
        final StringBuilder result = new StringBuilder( portalRequest.getBaseUri() );

        UrlBuilderHelper.appendSubPath( result, projectName.toString() );
        UrlBuilderHelper.appendSubPath( result, branch.toString() );

        final ContentPath contentPath = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> new ContentPathResolver().portalRequest( portalRequest )
                .contentService( this.contentService )
                .id( id )
                .path( path )
                .resolve() );

        UrlBuilderHelper.appendAndEncodePathParts( result, contentPath.toString() );

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, result.toString() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ContentService contentService;

        private PortalRequest portalRequest;

        private String id;

        private String path;

        private String urlType;

        private ProjectName projectName;

        private Branch branch;

        public Builder setContentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder setId( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder setPath( final String path )
        {
            this.path = path;
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

        public PageSiteRequestBaseUrlStrategy build()
        {
            return new PageSiteRequestBaseUrlStrategy( this );
        }
    }
}
