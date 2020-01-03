package com.enonic.xp.portal.impl.exception;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorPageBuilderTest
{
    private ResourceService resourceService;


    @BeforeEach
    public void setup()
    {
        resourceService = Mockito.mock( ResourceService.class );
    }

    @Test
    public void test_html_description()
        throws Exception
    {

        final StackTraceElement[] traceElements = new StackTraceElement[]{new StackTraceElement( "class", "method", "fileName", 1 )};

        final Exception cause = new Exception();
        cause.setStackTrace( traceElements );

        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( cause ).
            description( "<\"description\" \'with\' " + " escapable  text/> && </>" ).
            resourceService( resourceService ).
            status( 404 ).
            title( "title" );

        final String result = readResource( "desc_error_page_builder_test.html" );
        assertEquals( result, builder.build() );
    }

    @Test
    public void test_html_title()
        throws Exception
    {

        final StackTraceElement[] traceElements = new StackTraceElement[]{new StackTraceElement( "class", "method", "fileName", 1 )};

        final Exception cause = new Exception();
        cause.setStackTrace( traceElements );

        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( cause ).
            description( "desc" ).
            resourceService( resourceService ).
            status( 404 ).
            title( "<\"title\" \'with\' " + " escapable  text/> && </>" );

        final String result = readResource( "title_error_page_builder_test.html" );
        assertEquals( result, builder.build() );
    }

    @Test
    public void test_html_cause()
        throws Exception
    {

        final String classStr = "<\"class name\" 'with'  escapable  text/> && </>";
        final String methodStr = "<\"method name\" 'with'  escapable  text/> && </>";
        final String fileStr = "<\"file name\" 'with'  escapable  text/> && </>";

        final StackTraceElement traceElement = new StackTraceElement( classStr, methodStr, fileStr, 1 );
        final StackTraceElement[] traceElements = new StackTraceElement[]{traceElement};

        final Exception cause = new Exception();
        cause.setStackTrace( traceElements );

        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( cause ).
            description( "desc" ).
            resourceService( resourceService ).
            status( 404 ).
            title( "<\"title\" \'with\' " + " escapable  text/> && </>" );

        final String result = readResource( "cause_error_page_builder_test.html" );
        assertEquals( result, builder.build() );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        try (final InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
