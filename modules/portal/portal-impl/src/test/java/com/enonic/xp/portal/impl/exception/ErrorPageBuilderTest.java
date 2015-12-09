package com.enonic.xp.portal.impl.exception;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.resource.ResourceService;

import static org.junit.Assert.*;

public class ErrorPageBuilderTest
{
    private ResourceService resourceService;



    @Before
    public void setup()
    {
        resourceService = Mockito.mock( ResourceService.class );
    }

    @Test
    public void test_html_description() throws Exception {

        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( new Exception(  ) ).
            description( "<\"description\" \'with\' " +
                             " escapable  text/> && </>" ).
            resourceService( resourceService ).
            status( 404 ).
            title( "title" );

        final String result = readResource( "desc_error_page_builder_test.html" );
        assertEquals( result, builder.build() );
    }

    @Test
    public void test_html_title() throws Exception {

        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( new Exception(  ) ).
            description( "desc" ).
            resourceService( resourceService ).
            status( 404 ).
            title( "<\"title\" \'with\' " +
                             " escapable  text/> && </>" );

        final String result = readResource( "title_error_page_builder_test.html" );
        assertEquals( result, builder.build() );
    }



    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }


}
