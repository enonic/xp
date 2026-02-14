package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HandlerHelperTest
{
    @Test
    void testFindEndpointPath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/prePath/_/app/path" );

        assertEquals( "path", HandlerHelper.findEndpointPath( req, "app" ) );
    }

    @Test
    void testFindEndpoint()
    {
        final WebRequest req1 = new WebRequest();
        req1.setRawPath( "/path/_/service/app/name" );
        assertEquals( "service", HandlerHelper.findEndpoint( req1 ) );

        final WebRequest req2 = new WebRequest();
        req2.setRawPath( "/path/_/" );
        assertEquals( "", HandlerHelper.findEndpoint( req2 ) );

        final WebRequest req3 = new WebRequest();
        req3.setRawPath( "/path/_/com.enonic.app.myapp:api-key" );
        assertEquals( "com.enonic.app.myapp:api-key", HandlerHelper.findEndpoint( req3 ) );

        final WebRequest req4 = new WebRequest();
        req4.setRawPath( "/path/without/endpoint" );
        assertNull( HandlerHelper.findEndpoint( req4 ) );
    }

    @Test
    void testGetParameter()
    {
        final WebRequest req = new WebRequest();
        req.getParams().put( "k", "v" );

        assertEquals( "v", HandlerHelper.getParameter( req, "k" ) );
    }

    @Test
    void testGetParameterEmpty()
    {
        final WebRequest req = new WebRequest();

        assertNull( HandlerHelper.getParameter( req, "k" ) );
    }

    @Test
    void testHandleDefaultOptions()
    {
        PortalResponse portalResponse =
            HandlerHelper.handleDefaultOptions( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", portalResponse.getHeaders().get( "Allow" ) );
    }

    @Test
    void testGetBodyLength()
        throws Exception
    {
        Resource resource = mock( Resource.class );
        when( resource.getSize() ).thenReturn( 10L );

        assertEquals( 10L, HandlerHelper.getBodyLength( resource ) );

        ByteSource byteSource = mock( ByteSource.class );
        when( byteSource.size() ).thenReturn( 20L );
        assertEquals( 20L, HandlerHelper.getBodyLength( byteSource ) );

        Map<?, ?> map = mock( Map.class );
        assertNull( HandlerHelper.getBodyLength( map ) );

        byte[] bytes = new byte[10];
        assertEquals( 10L, HandlerHelper.getBodyLength( bytes ) );

        String body = "body";
        assertEquals( 4L, HandlerHelper.getBodyLength( body ) );

        assertEquals( 0L, HandlerHelper.getBodyLength( null ) );
    }

    @Test
    void testGetSize()
    {
        WebResponse webResponse = WebResponse.create().header( HttpHeaders.CONTENT_LENGTH, "100" ).build();

        assertEquals( 100, HandlerHelper.getSize( webResponse ) );

        webResponse = WebResponse.create().body( "body" ).build();
        assertEquals( 4, HandlerHelper.getSize( webResponse ) );
    }

    @Test
    void testInvalidProjectName()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveProjectName( "#!@$" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testProjectName()
    {
        assertEquals( ProjectName.from( "name" ), HandlerHelper.resolveProjectName( "name" ) );
    }

    @Test
    void testInvalidBranchName()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveBranch( "#!@$" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testBranchName()
    {
        assertEquals( Branch.from( "name" ), HandlerHelper.resolveBranch( "name" ) );
    }

    @Test
    void testInvalidApplicationKey()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveApplicationKey( "<>" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testApplicationName()
    {
        assertEquals( ApplicationKey.from( "name" ), HandlerHelper.resolveApplicationKey( "name" ) );
    }
}
