package com.enonic.xp.portal.impl.url;

import java.math.BigInteger;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

abstract class PortalUrlBuilder<T extends AbstractUrlParams>
{
    private static final Logger LOG = LoggerFactory.getLogger( PortalUrlBuilder.class );

    protected PortalRequest portalRequest;

    protected T params;

    protected ContentService contentService;

    protected ResourceService resourceService;

    protected ProjectService projectService;

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
        appendPart( str, this.portalRequest.getBaseUri() );

        final Multimap<String, String> params = LinkedListMultimap.create();
        buildUrl( str, params );
        appendParams( str, params.entries() );

        final String targetUri = str.toString();

        if ( !portalRequest.isSiteBase() )
        {
            final String baseUrl = resolveBaseUrl();
            if ( baseUrl != null )
            {
                return UrlTypeConstants.SERVER_RELATIVE.equals( this.params.getType() ) ? targetUri : baseUrl + targetUri;
            }
        }

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( portalRequest.getRawRequest(), targetUri );

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

    private SiteConfigs resolveSiteConfigs()
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final String contentPath = (String) context.getAttribute( "contentKey" );

        if ( contentPath != null )
        {
            final Site site = contentService.findNearestSiteByPath( ContentPath.from( contentPath ) );
            if ( site != null )
            {
                return site.getSiteConfigs();
            }
        }

        if ( context.getRepositoryId() != null )
        {
            final Project project = projectService.get( ProjectName.from( context.getRepositoryId() ) );
            if ( project != null )
            {
                return project.getSiteConfigs();
            }
        }

        return SiteConfigs.empty();
    }

    private String resolveBaseUrl()
    {
        final SiteConfig siteConfig = resolveSiteConfigs().get( ApplicationKey.from( "com.enonic.xp.site" ) );
        return siteConfig != null ? siteConfig.getConfig().getString( "baseUrl" ) : null;
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
            appendPart( url, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            appendPart( url, this.portalRequest.getBranch().toString() );
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
        appendPart( str, this.portalRequest.getBaseUri() );

        if ( this.portalRequest.isSiteBase() )
        {
            appendPart( str, RepositoryUtils.getContentRepoName( this.portalRequest.getRepositoryId() ) );
            appendPart( str, this.portalRequest.getBranch().toString() );
            appendPart( str, this.portalRequest.getContentPath().toString() );
        }

        appendPart( str, "_" );
        appendPart( str, "error" );
        appendPart( str, String.valueOf( code ) );

        final Multimap<String, String> params = LinkedListMultimap.create();

        if ( message != null )
        {
            params.put( "message", message );
        }

        appendParams( str, params.entries() );
        final String uri = str.toString();
        return ServletRequestUrlHelper.rewriteUri( portalRequest.getRawRequest(), uri ).getRewrittenUri();
    }

    @Override
    public final String toString()
    {
        return build();
    }
}
