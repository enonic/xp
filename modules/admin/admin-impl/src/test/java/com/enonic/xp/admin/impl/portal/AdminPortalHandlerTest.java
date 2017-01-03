package com.enonic.xp.admin.impl.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.Assert.*;

public class AdminPortalHandlerTest
    extends BaseHandlerTest
{

    private AdminPortalHandler handler;

    private WebRequest request;

    private WebResponse response;

    @Before
    public final void setup()
        throws Exception
    {
        final ExceptionMapper exceptionMapper = Mockito.mock( ExceptionMapper.class );
        final ExceptionRenderer exceptionRenderer = Mockito.mock( ExceptionRenderer.class );

        this.handler = new AdminPortalHandler();
        this.handler.setExceptionMapper( exceptionMapper );
        this.handler.setExceptionRenderer( exceptionRenderer );

        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );

        this.request = new WebRequest();
        this.request.setRawRequest( rawRequest );

        this.response = WebResponse.create().build();
    }

    @Test
    public void testCanHandle()
    {
        this.request.setRawPath( "/admin/portal/master/content/1" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCannotHandle()
    {
        this.request.setRawPath( "/admin/master/content/1" );
        assertFalse( this.handler.canHandle( this.request ) );
    }

    @Test(expected = WebException.class)
    public void testCreatePortalRequestWithoutMode()
    {
        this.request.setRawPath( "/admin/portal/master/content/1" );
        this.handler.createPortalRequest( this.request, this.response );
    }

    @Test
    public void testCreatePortalRequest()
    {
        this.request.setRawPath( "/admin/portal/edit/master/content/1" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/portal/edit", portalRequest.getBaseUri() );
        assertEquals( "master", portalRequest.getBranch().getName() );
        assertEquals( "/content/1", portalRequest.getContentPath().toString() );
        assertEquals( "edit", portalRequest.getMode().toString() );
    }
}
