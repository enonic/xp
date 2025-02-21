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

public class HandlerHelperTest
{
    @Test
    public void testFindPreRestPath()
    {
        final WebRequest req = mock( WebRequest.class );
        when( req.getRawPath() ).thenReturn( "/prePath/_/app/path" );

        assertEquals( "/prePath/_/app", HandlerHelper.findPreRestPath( req, "app" ) );
    }

    @Test
    public void testFindRestPath()
    {
        final WebRequest req = mock( WebRequest.class );
        when( req.getEndpointPath() ).thenReturn( "app" );
        when( req.getRawPath() ).thenReturn( "/prePath/_/app/path" );

        assertEquals( "", HandlerHelper.findRestPath( req, "app" ) );
    }

    @Test
    public void testGetParameter()
    {
        final WebRequest req = new WebRequest();
        req.getParams().put( "k", "v" );

        assertEquals( "v", HandlerHelper.getParameter( req, "k" ) );
    }

    @Test
    public void testGetParameterEmpty()
    {
        final WebRequest req = new WebRequest();

        assertNull( HandlerHelper.getParameter( req, "k" ) );
    }

    @Test
    public void testHandleDefaultOptions()
    {
        PortalResponse portalResponse =
            HandlerHelper.handleDefaultOptions( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );

        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", portalResponse.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testGetBodyLength()
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
    public void testGetSize()
    {
        WebResponse webResponse = WebResponse.create().header( HttpHeaders.CONTENT_LENGTH, "100" ).build();

        assertEquals( 100, HandlerHelper.getSize( webResponse ) );

        webResponse = WebResponse.create().body( "body" ).build();
        assertEquals( 4, HandlerHelper.getSize( webResponse ) );
    }

    @Test
    public void testInvalidProjectName()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveProjectName( "#!@$" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testProjectName()
    {
        assertEquals( ProjectName.from( "name" ), HandlerHelper.resolveProjectName( "name" ) );
    }

    @Test
    public void testInvalidBranchName()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveBranch( "#!@$" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testBranchName()
    {
        assertEquals( Branch.from( "name" ), HandlerHelper.resolveBranch( "name" ) );
    }

    @Test
    public void testInvalidApplicationKey()
    {
        final WebException ex = assertThrows( WebException.class, () -> HandlerHelper.resolveApplicationKey( "<>" ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testApplicationName()
    {
        assertEquals( ApplicationKey.from( "name" ), HandlerHelper.resolveApplicationKey( "name" ) );
    }
}
