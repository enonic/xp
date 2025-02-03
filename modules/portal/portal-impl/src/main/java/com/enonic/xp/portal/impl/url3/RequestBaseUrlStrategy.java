package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import javax.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class RequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final PortalRequest portalRequest;

    private final String urlType;

    public RequestBaseUrlStrategy( final PortalRequest portalRequest, final String urlType )
    {
        this.portalRequest = Objects.requireNonNull( portalRequest );
        this.urlType = Objects.requireNonNullElse( urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        if ( UrlTypeConstants.ABSOLUTE.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( portalRequest.getRawRequest() );
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
            } );
        }
        else
        {
            return null;
        }
    }
}
