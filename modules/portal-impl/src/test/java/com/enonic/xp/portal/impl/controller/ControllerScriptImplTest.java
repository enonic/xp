package com.enonic.xp.portal.impl.controller;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class ControllerScriptImplTest
    extends AbstractControllerTest
{
    @Before
    public void setUp()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void testExecute()
    {
        this.context.setMethod( "GET" );
        execute( "mymodule:/service/test" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
    }

    @Test
    public void testExecutePostProcess()
    {
        this.context.setMethod( "GET" );
        this.response.setPostProcess( true );

        execute( "mymodule:/service/test" );

        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        Mockito.verify( this.postProcessor ).processResponse( this.context );
    }

    @Test
    public void testMethodNotSupported()
    {
        this.context.setMethod( "POST" );
        execute( "mymodule:/service/test" );
        assertEquals( PortalResponse.STATUS_METHOD_NOT_ALLOWED, this.response.getStatus() );
    }

    @Test
    public void testGetterAccess()
    {
        this.context.setMethod( "GET" );
        execute( "mymodule:/service/getters" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        assertEquals( "GET,live", this.response.getBody() );
    }

    @Test
    public void testContributions()
    {
        this.context.setMethod( "GET" );
        execute( "mymodule:/service/contribute" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        assertEquals( 1, this.response.getContributions( "foot" ).size() );
        assertEquals( "<link href=\"styles.css\" rel=\"stylesheet\" />", this.response.getContributions( "foot" ).get( 0 ) );
        assertEquals( 1, this.response.getContributions( "head" ).size() );
        assertEquals( "<script src=\"test.js\" />", this.response.getContributions( "head" ).get( 0 ) );
        assertTrue( this.response.getContributions( "other" ).isEmpty() );
    }
}
