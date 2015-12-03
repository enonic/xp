package com.enonic.xp.admin.impl.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.BaseHandlerTest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class AdminApplicationHandlerTest
    extends BaseHandlerTest
{
    private ControllerScript controllerScript;

    private AdminApplicationHandler handler;

    @Override
    protected void configure()
        throws Exception
    {
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.fromDir( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        final PortalResponse portalResponse = PortalResponse.create().build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );

        this.handler = new AdminApplicationHandler();
        this.handler.setControllerScriptFactory( controllerScriptFactory );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/" ) );
        this.request.setEndpointPath( "/_/adminapps/demo/myadminapp" );
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

        this.request.setEndpointPath( "/_/other/demo/myadminapp" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/adminapps/demo/myadminapp" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/adminapps/demo/myadminapp" );
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
        this.request.setEndpointPath( "/_/adminapps/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid service url pattern", e.getMessage() );
        }
    }

    @Test(expected = PortalException.class)
    public void executeFailsWithWrongMode()
        throws Exception
    {
        this.request.setEndpointPath( "/_/adminapps/demo/test" );
        this.request.setMode( RenderMode.EDIT );

        final PortalResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        this.request.setEndpointPath( "/_/adminapps/demo/test" );
        this.request.setMode( RenderMode.ADMIN );

        final PortalResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNull( this.request.getSite() );
        assertNull( this.request.getContent() );
    }
}
