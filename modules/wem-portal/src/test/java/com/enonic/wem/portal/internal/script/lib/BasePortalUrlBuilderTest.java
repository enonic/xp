package com.enonic.wem.portal.internal.script.lib;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

public abstract class BasePortalUrlBuilderTest
{
    protected HttpServletRequest request;

    protected String baseUrl;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
        setupRequest( "http", "localhost", 8080, null );
        this.baseUrl = ServletRequestUrlHelper.createUri( "" );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
