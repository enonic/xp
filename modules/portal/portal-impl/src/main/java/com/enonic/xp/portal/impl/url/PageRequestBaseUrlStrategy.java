package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.rewriteUri;

final class PageRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String id;

    private final String path;

    private final String urlType;

    private PageRequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.id = builder.id;
        this.path = builder.path;
    }

    @Override
    public String generateBaseUrl()
    {
        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( portalRequest.isSiteBase() )
        {
            appendPart( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            appendPart( uriBuilder, portalRequest.getBranch().getValue() );
        }

        final ContentPath contentPath = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( portalRequest.getRepositoryId() )
            .branch( portalRequest.getBranch() )
            .build()
            .callWith( () -> new ContentPathResolver().portalRequest( portalRequest )
                .contentService( this.contentService )
                .id( id )
                .path( path )
                .resolve() );

        appendPart( uriBuilder, contentPath.toString() );

        return rewriteUri( portalRequest.getRawRequest(), urlType, uriBuilder.toString() );
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

        public PageRequestBaseUrlStrategy build()
        {
            return new PageRequestBaseUrlStrategy( this );
        }
    }
}
