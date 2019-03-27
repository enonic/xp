package com.enonic.xp.portal.impl.handler.identity;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

public class IdentityHandlerTest
    extends BaseHandlerTest
{
    private IdentityHandler handler;

    private PortalRequest request;

    private String virtualHostKey;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ContentService contentService = Mockito.mock( ContentService.class );
        final IdProviderControllerService idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );

        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenAnswer( invocation -> {
            Object[] args = invocation.getArguments();
            final IdProviderControllerExecutionParams arg = (IdProviderControllerExecutionParams) args[0];
            if ( IdProviderKey.from( "myidprovider" ).equals( arg.getIdProviderKey() ) && "get".equals( arg.getFunctionName() ) )
            {
                return PortalResponse.create().build();
            }
            return null;
        } );

        this.handler = new IdentityHandler();
        this.handler.setContentService( contentService );
        this.handler.setIdProviderControllerService( idProviderControllerService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setEndpointPath( "/_/idprovider/myidprovider?param1=value1" );
        this.request.setRawPath( "/site/draft/_/idprovider/myidprovider?param1=value1" );
        this.request.setRawRequest( rawRequest );
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
        final IdProviderControllerService idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        final PortalResponse response = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( response );
        this.handler.setIdProviderControllerService( idProviderControllerService );

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
        assertEquals( "/site/draft/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    public void testHandleWithVirtualHostNotEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
        }
        catch ( final WebException e )
        {
            assertEquals( "'myidprovider' id provider is forbidden", e.getMessage() );
        }
    }

    @Test
    public void testHandleWithVirtualHostEnabled()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider", "myidprovider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );

        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build(), null );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/draft/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    @Test
    public void testHandleWithEmptyVirtualHostIdProviderConfig()
        throws Exception
    {
        final HttpServletRequest rawRequest = this.request.getRawRequest();

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.empty() );

        VirtualHostHelper.setVirtualHost( rawRequest, virtualHost );

        final WebResponse portalResponse = this.handler.handle( this.request, PortalResponse.create().build(), null );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "/site/draft/_/idprovider/myidprovider", this.request.getContextPath() );
    }

    public VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        Mockito.doAnswer( ( InvocationOnMock invocation ) -> {
            return virtualHostKey = (String) invocation.getArguments()[0];

        } ).when( rawRequest ).setAttribute( Mockito.any(), Mockito.isA( VirtualHost.class ) );

        Mockito.when( rawRequest.getAttribute( Mockito.isA( String.class ) ) ).thenAnswer( ( InvocationOnMock invocation ) -> {

            return virtualHostKey.equals( invocation.getArguments()[0] ) ? virtualHost : null;

        } );

        return virtualHost;
    }
}
