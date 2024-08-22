package com.enonic.xp.portal.impl.handler;

import java.util.regex.Pattern;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.web.WebRequest;

final class DefaultContextPathVerifier
{
    private static final Pattern WEBAPP_DEFAULT_CONTEXT_PATH_PATTERN = Pattern.compile( "^/webapp/([^/]+)/_/.*" );

    private final ContentService contentService;

    DefaultContextPathVerifier( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public boolean verify( final WebRequest webRequest )
    {
        final String rawPath = webRequest.getRawPath();

        if ( rawPath.startsWith( "/site/" ) || rawPath.startsWith( "/admin/site/" ) )
        {
            final PortalRequest portalRequest =
                webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );

            return "/".equals( contentResolverResult.getSiteRelativePath() );
        }
        else if ( rawPath.startsWith( "/admin/" ) )
        {
            return rawPath.startsWith( "/admin/_/" );
        }
        else if ( rawPath.startsWith( "/webapp/" ) )
        {
            return WEBAPP_DEFAULT_CONTEXT_PATH_PATTERN.matcher( rawPath ).matches();
        }
        else if ( rawPath.startsWith( "/api/" ) )
        {
            return webRequest.getEndpointPath() == null;
        }
        else
        {
            return false;
        }
    }
}
