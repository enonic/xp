package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import javax.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

final class RequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String urlType;

    private RequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        final String uri = generateUri();

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( portalRequest.getRawRequest(), uri );

        if ( rewritingResult.isOutOfScope() )
        {
            throw new OutOfScopeException( "URI out of scope" );
        }

        final String rewrittenUri = rewritingResult.getRewrittenUri();

        if ( UrlTypeConstants.ABSOLUTE.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( portalRequest.getRawRequest() ) + rewrittenUri;
        }
        else if ( UrlTypeConstants.WEBSOCKET.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( new HttpServletRequestWrapper( portalRequest.getRawRequest() )
            {
                @Override
                public String getScheme()
                {
                    return isSecure() ? "wss" : "ws";
                }
            } ) + rewrittenUri;
        }
        else
        {
            return rewrittenUri;
        }
    }

    private String generateUri()
    {
        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( portalRequest.isSiteBase() )
        {
            appendPart( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            appendPart( uriBuilder, portalRequest.getBranch().getValue() );
            appendPart( uriBuilder, resolveSitePath().toString() );
        }

        appendPart( uriBuilder, "_" );

        return uriBuilder.toString();
    }

    private ContentPath resolveSitePath()
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( portalRequest.getRepositoryId() )
            .branch( portalRequest.getBranch() )
            .build();

        Site nearestSite = context.callWith( () -> {
            final ContentResolver contentResolver = new ContentResolver( contentService );
            return contentResolver.resolve( portalRequest ).getNearestSite();
        } );

        return nearestSite != null ? nearestSite.getPath() : ContentPath.ROOT;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ContentService contentService;

        private PortalRequest portalRequest;

        private String urlType;

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder portalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder urlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public RequestBaseUrlStrategy build()
        {
            return new RequestBaseUrlStrategy( this );
        }
    }
}
