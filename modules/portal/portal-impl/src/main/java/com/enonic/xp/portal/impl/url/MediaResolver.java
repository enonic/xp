package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.ProjectName;

final class MediaResolver
{
    private final ProjectName projectName;

    private final Branch branch;

    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String baseUrl;

    private final String id;

    private final String path;

    private MediaResolver( final Builder builder )
    {
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = builder.portalRequest;
        this.baseUrl = builder.baseUrl;
        this.id = builder.id;
        this.path = builder.path;
    }

    public MediaResolverResult resolve()
    {
        final String contentKey = Objects.requireNonNullElseGet( id, () -> {
            if ( portalRequest == null || baseUrl != null )
            {
                return Objects.requireNonNull( path );
            }
            return Objects.requireNonNullElseGet( path, () -> portalRequest.getContentPath().toString() );
        } );

        final Content content = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> {
                if ( contentKey.startsWith( "/" ) )
                {
                    return contentService.getByPath( ContentPath.from( contentKey ) );
                }
                else
                {
                    return contentService.getById( ContentId.from( contentKey ) );
                }
            } );

        return new MediaResolverResult( content, contentKey );
    }

    public static Builder create( final ProjectName projectName, final Branch branch, final ContentService contentService )
    {
        return new Builder( projectName, branch, contentService );
    }

    static class Builder
    {
        private final ProjectName projectName;

        private final Branch branch;

        private final ContentService contentService;

        private PortalRequest portalRequest;

        private String baseUrl;

        private String id;

        private String path;

        Builder( final ProjectName projectName, final Branch branch, final ContentService contentService )
        {
            this.projectName = projectName;
            this.branch = branch;
            this.contentService = contentService;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
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

        public MediaResolver build()
        {
            return new MediaResolver( this );
        }
    }
}
