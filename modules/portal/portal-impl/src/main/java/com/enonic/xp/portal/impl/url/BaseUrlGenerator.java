package com.enonic.xp.portal.impl.url;

import javax.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.UrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public abstract class BaseUrlGenerator<T extends UrlParams>
    implements UrlGenerator<T>
{

    protected final ContentService contentService;

    protected BaseUrlGenerator( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public abstract String doGenerateUrl( T params );

    @Override
    public final String generateUrl( final T params )
    {
        final String targetUrl = doGenerateUrl( params );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( params.getWebRequest().getRawRequest(), targetUrl );

        if ( rewritingResult.isOutOfScope() )
        {
            throw new OutOfScopeException( "URI out of scope" );
        }

        final String baseUrl = resolveBaseUrl( params.getWebRequest(), params.getUrlType() );
        final String rewrittenUri = rewritingResult.getRewrittenUri(); // create function with using postProcessUri

        // should be moved to function to resolve conflicts with baseURL and replace parts of URL if needed
        final StringBuilder url = new StringBuilder();
        appendPart( url, baseUrl );
        appendPart( url, rewrittenUri );
        return url.toString();
    }

    private String resolveBaseUrl( final WebRequest webRequest, final String urlType )
    {
        if ( UrlTypeConstants.SERVER_RELATIVE.equals( urlType ) )
        {
            return null;
        }

        if ( webRequest == null )
        {
            // TODO try to find baseUrl from Context
            // 1. Try to find com.enonic.xp.repository.RepositoryId from Context
            // 2. If found, try to find com.enonic.xp.site -> baseUrl in the projectConfigs aka siteConfigs from Project by RepositoryId and Branch (master is default branch)
            // 3. If found, return baseUrl
            // 4. If not found, try to find baseUrl from Context
            // 5. If not found, return null, otherwise return baseUrl
            // null means that URL is relative to the server
            return null;
        }

        if ( UrlTypeConstants.ABSOLUTE.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( webRequest.getRawRequest() );
        }
        else if ( UrlTypeConstants.WEBSOCKET.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( new HttpServletRequestWrapper( webRequest.getRawRequest() )
            {
                @Override
                public String getScheme()
                {
                    return isSecure() ? "wss" : "ws";
                }
            } );
        }
        else
        {
            return null;
        }
    }
}
