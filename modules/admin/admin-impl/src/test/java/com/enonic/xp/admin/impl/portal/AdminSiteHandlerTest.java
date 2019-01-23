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

public class AdminSiteHandlerTest
    extends BaseHandlerTest
{

    private AdminSiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    @Before
    public final void setup()
        throws Exception
    {
        final ExceptionMapper exceptionMapper = Mockito.mock( ExceptionMapper.class );
        final ExceptionRenderer exceptionRenderer = Mockito.mock( ExceptionRenderer.class );

        this.handler = new AdminSiteHandler();
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
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testCannotHandle()
    {
        this.request.setRawPath( "/admin/repo/master/content/1" );
        assertFalse( this.handler.canHandle( this.request ) );
    }

    @Test(expected = WebException.class)
    public void testCreatePortalRequestWithoutMode()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        this.handler.createPortalRequest( this.request, this.response );
    }

    @Test
    public void testCreatePortalRequest()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/content/1" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/content/1", portalRequest.getContentPath().toString() );
        assertEquals( "edit", portalRequest.getMode().toString() );
    }
}
