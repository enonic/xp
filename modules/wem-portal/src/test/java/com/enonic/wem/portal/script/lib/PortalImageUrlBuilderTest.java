package com.enonic.wem.portal.script.lib;


import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.web.servlet.ServletRequestHolder;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

import static org.junit.Assert.*;

public class PortalImageUrlBuilderTest
{
    private HttpServletRequest request;

    private String baseUrl;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
        setupRequest( "http", "localhost", 8080, null );
        this.baseUrl = ServletRequestUrlHelper.createUrl( "" );
    }

    @Test
    public void createImageUrl()
    {
        final PortalImageUrlBuilder urlBuilder = PortalImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            resourcePath( "pop_08.jpg" ).
            filter( "scalemax(120)" ).
            filters( Lists.newArrayList( "rounded(40)" ) ).
            filters( "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( "33" );

        assertEquals( "http://localhost:8080/portal/live/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
