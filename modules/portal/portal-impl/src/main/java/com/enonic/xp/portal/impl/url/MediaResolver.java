package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectName;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

final class MediaResolver
{
    private final ProjectName projectName;

    private final Branch branch;

    private final ContentService contentService;

    private final String baseUrl;

    private final String id;

    private final String path;

    private MediaResolver( final Builder builder )
    {
        this.projectName = requireNonNull( builder.projectName );
        this.branch = requireNonNull( builder.branch );
        this.contentService = requireNonNull( builder.contentService );
        this.baseUrl = builder.baseUrl;
        this.id = builder.id;
        this.path = builder.path;
    }

    public MediaResolverResult resolve()
    {
        final String contentKey = requireNonNullElseGet( id, () -> {
            if ( baseUrl != null )
            {
                return requireNonNull( path );
            }

            return requireNonNullElseGet( path, () -> {
                final PortalRequest portalRequest = PortalRequestAccessor.get();
                if ( PortalRequestHelper.isSiteBase( portalRequest ) )
                {
                    return portalRequest.getContentPath().toString();
                }
                return null;
            } );
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

        private String baseUrl;

        private String id;

        private String path;

        Builder( final ProjectName projectName, final Branch branch, final ContentService contentService )
        {
            this.projectName = projectName;
            this.branch = branch;
            this.contentService = contentService;
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
