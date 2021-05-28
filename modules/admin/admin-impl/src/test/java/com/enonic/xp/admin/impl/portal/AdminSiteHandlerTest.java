package com.enonic.xp.admin.impl.portal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminSiteHandlerTest
    extends BaseHandlerTest
{
    private AdminSiteHandler handler;

    private WebRequest request;

    private WebResponse response;

    @BeforeEach
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

    @Test
    public void testCreatePortalRequestWithoutMode()
    {
        this.request.setRawPath( "/admin/site/repo/master/content/1" );
        assertThrows(WebException.class, () -> this.handler.createPortalRequest( this.request, this.response ));
    }

    @Test
    public void testCreatePortalRequest()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/content/1" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/edit", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "master", portalRequest.getBranch().toString() );
        assertEquals( "/content/1", portalRequest.getContentPath().toString() );
        assertEquals( "edit", portalRequest.getMode().toString() );
    }

    @Test
    public void testInlineAssetRequest()
    {
        this.request.setRawPath( "/admin/site/inline/repo/draft/_/asset/com.enonic.app.superhero:1622131535374/css/style.css" );
        PortalRequest portalRequest = this.handler.createPortalRequest( this.request, this.response );

        assertEquals( "/admin/site/inline", portalRequest.getBaseUri() );
        assertEquals( "com.enonic.cms.repo", portalRequest.getRepositoryId().toString() );
        assertEquals( "draft", portalRequest.getBranch().toString() );
        assertEquals( "/", portalRequest.getContentPath().toString() );
        assertEquals( "inline", portalRequest.getMode().toString() );
    }

    @Test
    public void testCreatePortalRequestEmptyContentPath()
    {
        this.request.setRawPath( "/admin/site/edit/repo/master/" );
        WebException exception = assertThrows( WebException.class, () -> this.handler.createPortalRequest( this.request, this.response ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );

        this.request.setRawPath( "/admin/site/edit/repo/master" );
        exception = assertThrows( WebException.class, () -> this.handler.createPortalRequest( this.request, this.response ) );
        assertEquals( HttpStatus.NOT_FOUND, exception.getStatus() );
    }
}
