package com.enonic.xp.web.impl.serializer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestSerializerMultipartTest
{
    private HttpServletRequest request;

    @BeforeEach
    void setUp()
    {
        request = mock( HttpServletRequest.class );
        when( request.getMethod() ).thenReturn( "POST" );
        when( request.getScheme() ).thenReturn( "http" );
        when( request.getServerName() ).thenReturn( "localhost" );
        when( request.getServerPort() ).thenReturn( 8080 );
        when( request.getRequestURI() ).thenReturn( "/site/master/a" );
        when( request.getPathInfo() ).thenReturn( "/site/master/a" );
        when( request.getLocales() ).thenReturn( Collections.emptyEnumeration() );
        when( request.getHeaderNames() ).thenReturn( Collections.emptyEnumeration() );
    }

    @Test
    void multipartRequestTakesParametersFromQueryStringOnly()
        throws Exception
    {
        when( request.getContentType() ).thenReturn( "multipart/form-data; boundary=----boundary" );
        when( request.getQueryString() ).thenReturn( "a=1&a=2&b=x+y&c=%C3%A6&d" );

        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( request );

        assertEquals( List.of( "1", "2" ), webRequest.getParams().get( "a" ) );
        assertEquals( List.of( "x y" ), webRequest.getParams().get( "b" ) );
        assertEquals( List.of( "\u00e6" ), webRequest.getParams().get( "c" ) );
        assertEquals( List.of( "" ), webRequest.getParams().get( "d" ) );
        verify( request, never() ).getParameterMap();
        verify( request, never() ).getParts();
    }

    @Test
    void multipartRequestWithoutQueryString()
    {
        when( request.getContentType() ).thenReturn( "Multipart/Form-Data; boundary=x" );
        when( request.getQueryString() ).thenReturn( null );

        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( request );

        assertTrue( webRequest.getParams().isEmpty() );
        verify( request, never() ).getParameterMap();
    }

    @Test
    void multipartRequestWithMalformedQueryString()
    {
        when( request.getContentType() ).thenReturn( "multipart/form-data; boundary=x" );
        when( request.getQueryString() ).thenReturn( "a=%zz" );

        final WebException e = assertThrows( WebException.class, () -> new RequestSerializer( new WebRequest() ).serialize( request ) );
        assertEquals( HttpStatus.BAD_REQUEST, e.getStatus() );
    }

    @Test
    void formRequestUsesParameterMap()
    {
        when( request.getContentType() ).thenReturn( "application/x-www-form-urlencoded" );
        when( request.getParameterMap() ).thenReturn( Map.of( "field", new String[]{"value"} ) );

        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( request );

        assertEquals( List.of( "value" ), webRequest.getParams().get( "field" ) );
    }

    @Test
    void multipartMixedRequestTakesParametersFromQueryStringOnly()
        throws Exception
    {
        when( request.getContentType() ).thenReturn( "multipart/mixed; boundary=x" );
        when( request.getQueryString() ).thenReturn( "a=1" );

        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( request );

        assertEquals( List.of( "1" ), webRequest.getParams().get( "a" ) );
        verify( request, never() ).getParameterMap();
        verify( request, never() ).getParts();
    }

    @Test
    void isMultipart()
    {
        assertTrue( RequestSerializer.isMultipart( "multipart/form-data" ) );
        assertTrue( RequestSerializer.isMultipart( "multipart/form-data; boundary=x" ) );
        assertTrue( RequestSerializer.isMultipart( "MULTIPART/FORM-DATA ; boundary=x" ) );
        assertTrue( RequestSerializer.isMultipart( "multipart/mixed; boundary=x" ) );
        assertTrue( RequestSerializer.isMultipart( "multipart/related; type=application/xop+xml" ) );
        assertTrue( RequestSerializer.isMultipart( " Multipart/Byteranges" ) );
        assertFalse( RequestSerializer.isMultipart( "multipartx/form-data" ) );
        assertFalse( RequestSerializer.isMultipart( "application/x-www-form-urlencoded" ) );
        assertFalse( RequestSerializer.isMultipart( "text/plain" ) );
        assertFalse( RequestSerializer.isMultipart( "" ) );
        assertFalse( RequestSerializer.isMultipart( null ) );
    }
}
