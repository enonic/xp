package com.enonic.xp.portal.impl.handler.identity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.Assert.*;

public class IdentityHandlerTest
    extends BaseHandlerTest
{
    private IdentityHandler handler;

    private PortalRequest request;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ContentService contentService = Mockito.mock( ContentService.class );
        final AuthControllerService authControllerService = Mockito.mock( AuthControllerService.class );

        Mockito.when( authControllerService.execute( Mockito.any() ) ).thenAnswer( invocation -> {
            Object[] args = invocation.getArguments();
            final AuthControllerExecutionParams arg = (AuthControllerExecutionParams) args[0];
            if ( UserStoreKey.from( "myuserstore" ).equals( arg.getUserStoreKey() ) && "get".equals( arg.getFunctionName() ) )
            {
                return PortalResponse.create().build();
            }
            return null;
        } );

        this.handler = new IdentityHandler();
        this.handler.setContentService( contentService );
        this.handler.setAuthControllerService( authControllerService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setEndpointPath( "/_/idprovider/myuserstore?param1=value1" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/idprovider/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/idprovider/a/b" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        final AuthControllerService authControllerService = Mockito.mock( AuthControllerService.class );
        final PortalResponse response = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        Mockito.when( authControllerService.execute( Mockito.any() ) ).thenReturn( response );
        this.handler.setAuthControllerService( authControllerService );

        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/idprovider/" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid idprovider url pattern", e.getMessage() );
        }
    }

    @Test
    public void testHandle()
        throws Exception
    {
        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build(), null );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
    }
}
