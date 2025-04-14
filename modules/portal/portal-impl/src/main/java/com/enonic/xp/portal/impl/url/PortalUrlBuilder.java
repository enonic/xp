package com.enonic.xp.portal.impl.url;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

abstract class PortalUrlBuilder<T extends AbstractUrlParams>
{
    private static final Logger LOG = LoggerFactory.getLogger( PortalUrlBuilder.class );

    protected PortalRequest portalRequest;

    protected T params;

    protected ContentService contentService;

    protected ResourceService resourceService;

    public final void setParams( final T params )
    {
        this.params = params;
        this.portalRequest = this.params.getPortalRequest();
    }

    public final String build()
    {
        try
        {
            return doBuild();
        }
        catch ( final Exception e )
        {
            return buildErrorUrl( e );
        }
    }

    private String doBuild()
    {
        final StringBuilder str = new StringBuilder();
        UrlBuilderHelper.appendSubPath( str, this.portalRequest.getBaseUri() );

        final Multimap<String, String> params = LinkedListMultimap.create();
        buildUrl( str, params );
        UrlBuilderHelper.appendParams( str, params.entries() );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( portalRequest.getRawRequest(), str.toString() );

        if ( rewritingResult.isOutOfScope() )
        {
            throw new OutOfScopeException( "URI out of scope" );
        }

        final String uri = postUriRewriting( rewritingResult );

        if ( UrlTypeConstants.ABSOLUTE.equals( this.params.getType() ) )
        {
            return ServletRequestUrlHelper.getServerUrl( portalRequest.getRawRequest() ) + uri;
        }
        else if ( UrlTypeConstants.WEBSOCKET.equals( this.params.getType() ) )
        {
            return ServletRequestUrlHelper.getServerUrl( webSocketRequestWrapper( portalRequest.getRawRequest() ) ) + uri;
        }
        else
        {
            return uri;
        }
    }

    private static HttpServletRequestWrapper webSocketRequestWrapper( final HttpServletRequest request )
    {
        return new HttpServletRequestWrapper( request )
        {
            @Override
            public String getScheme()
            {
                return isSecure() ? "wss" : "ws";
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        params.putAll( this.params.getParams() );

        if ( this.portalRequest.isSiteBase() )
        {
            UrlBuilderHelper.appendSubPath( url, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            UrlBuilderHelper.appendSubPath( url, this.portalRequest.getBranch().toString() );
        }
    }

    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
        return uriRewritingResult.getRewrittenUri();
    }

    protected String buildErrorUrl( final Exception e )
    {
        final String logRef = LOG.isWarnEnabled() ? newLogRef() : "";

        LOG.warn( "Portal url build failed. Logref: {}", logRef, e );
        if ( e instanceof NotFoundException )
        {
            return buildErrorUrl( 404, String.join( " ", "Not Found.", logRef ) );
        }
        else if ( e instanceof OutOfScopeException )
        {
            return buildErrorUrl( 400, String.join( " ", "Out of scope.", logRef ) );
        }
        else
        {
            return buildErrorUrl( 500, String.join( " ", "Something went wrong.", logRef ) );
        }
    }

    private static String newLogRef()
    {
        return new BigInteger( UUID.randomUUID().toString().replace( "-", "" ), 16 ).toString( 32 );
    }

    protected final String buildErrorUrl( final int code, final String message )
    {
        final StringBuilder str = new StringBuilder();
        UrlBuilderHelper.appendSubPath( str, this.portalRequest.getBaseUri() );

        if ( this.portalRequest.isSiteBase() )
        {
            UrlBuilderHelper.appendSubPath( str, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            UrlBuilderHelper.appendSubPath( str, this.portalRequest.getBranch().toString() );
            UrlBuilderHelper.appendSubPath( str, this.portalRequest.getContentPath().toString() );
        }

        UrlBuilderHelper.appendSubPath( str, "/_/error/" + code );

        final Multimap<String, String> params = LinkedListMultimap.create();

        if ( message != null )
        {
            params.put( "message", message );
        }

        UrlBuilderHelper.appendParams( str, params.entries() );
        final String uri = str.toString();
        return ServletRequestUrlHelper.createUri( portalRequest.getRawRequest(), uri );
    }

    @Override
    public final String toString()
    {
        return build();
    }
}
