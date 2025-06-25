package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

final class ApiUrlBaseUrlResolver
    implements Supplier<String>
{
    private final ContentService contentService;

    private final ProjectService projectService;

    private final DescriptorKey descriptorKey;

    private final String baseUrl;

    private final String urlType;

    ApiUrlBaseUrlResolver( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService, "ContentService must be set" );
        this.projectService = Objects.requireNonNull( builder.projectService, "ProjectService must be set" );
        this.descriptorKey = Objects.requireNonNull( builder.descriptorKey, "DescriptorKey must be set" );
        this.baseUrl = builder.baseUrl;
        this.urlType = builder.urlType;
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private ContentService contentService;

        private ProjectService projectService;

        private DescriptorKey descriptorKey;

        private String baseUrl;

        private String urlType;

        Builder setContentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        Builder setProjectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        Builder setDescriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        ApiUrlBaseUrlResolver build()
        {
            return new ApiUrlBaseUrlResolver( this );
        }
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final StringBuilder url = new StringBuilder( generateBaseUrlPrefix( portalRequest ) );

        UrlBuilderHelper.appendPart( url, descriptorKey.toString() );

        if ( baseUrl != null )
        {
            return url.toString();
        }
        else if ( portalRequest != null && !portalRequest.getBaseUri().isEmpty() )
        {
            return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, url.toString() );
        }
        else
        {
            return url.toString();
        }
    }

    private String generateBaseUrlPrefix( final PortalRequest portalRequest )
    {
        final StringBuilder url = new StringBuilder();

        if ( baseUrl != null )
        {
            url.append( baseUrl );
            UrlBuilderHelper.appendPart( url, "_" );
        }
        else if ( portalRequest == null || portalRequest.getBaseUri().isEmpty() || portalRequest.getBaseUri().startsWith( "/api/" ) )
        {
            url.append( "/api" );
        }
        else if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            url.append( portalRequest.getBaseUri() );
            UrlBuilderHelper.appendSubPath( url, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( url, portalRequest.getBranch().getValue() );

            final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( portalRequest.getRepositoryId() )
                .branch( portalRequest.getBranch() )
                .build();

            final Site nearestSite = context.callWith( () -> {
                final ContentResolver contentResolver = new ContentResolver( contentService, projectService );
                return contentResolver.resolve( portalRequest ).getNearestSite();
            } );

            if ( nearestSite != null )
            {
                UrlBuilderHelper.appendAndEncodePathParts( url, nearestSite.getPath().toString() );
            }

            UrlBuilderHelper.appendPart( url, "_" );
        }
        else if ( portalRequest.getBaseUri().equals( "/admin" ) )
        {
            url.append( "/admin/com.enonic.xp.app.main/home/_/" );
        }
        else
        {
            url.append( portalRequest.getBaseUri() );
            UrlBuilderHelper.appendPart( url, "_" );
        }

        return url.toString();
    }
}
