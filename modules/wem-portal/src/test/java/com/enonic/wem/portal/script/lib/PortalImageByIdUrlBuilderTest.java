package com.enonic.wem.portal.script.lib;


import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

import static org.junit.Assert.*;

public class PortalImageByIdUrlBuilderTest
{
    private HttpServletRequest request;

    private String baseUrl;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
        setupRequest( "http", "localhost", 8080, null );
        this.baseUrl = ServletRequestUrlHelper.createUri( "" );
    }

    @Test
    public void createImageUrlWithoutFilters()
    {
        final PortalImageByIdUrlBuilder urlBuilder = PortalImageByIdUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "mypage" ).
            imageContent( ContentId.from( "abc" ) ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/mypage/_/image/id/abc",
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
