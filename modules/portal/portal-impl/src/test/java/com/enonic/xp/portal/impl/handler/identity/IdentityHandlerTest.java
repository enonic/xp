package com.enonic.xp.portal.impl.handler.identity;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.portal.handler.BaseHandlerTest;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class IdentityHandlerTest
    extends BaseHandlerTest
{
    private IdentityHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
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
        this.request.setMethod( HttpMethod.OPTIONS );

        final PortalResponse res = this.handler.handle( this.request );
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
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid idprovider url pattern", e.getMessage() );
        }
    }

    @Test
    public void testHandle()
        throws Exception
    {
        final PortalResponse portalResponse = this.handler.handle( this.request );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
    }
}
