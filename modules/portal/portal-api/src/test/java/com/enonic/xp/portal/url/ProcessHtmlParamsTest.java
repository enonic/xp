package com.enonic.xp.portal.url;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProcessHtmlParamsTest
{
    @Test
    public void testValue()
    {
        final ProcessHtmlParams params = new ProcessHtmlParams();
        assertNull( params.getValue() );

        params.value( "" );
        assertNull( params.getValue() );

        params.value( "<html/>" );
        assertEquals( "<html/>", params.getValue() );
    }

    @Test
    public void testSetAsMap()
    {
        final ProcessHtmlParams params = new ProcessHtmlParams();
        params.value( "<html/>" );
        params.imageWidths( List.of( 600, 1024 ) );
        params.param( "a", "1" );
        params.imageSizes( "sizes" );

        assertEquals( "<html/>", params.getValue() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ProcessHtmlParams{type=server, params={a=[1]}, value=<html/>, imageWidths=[600, 1024], imageSizes=sizes}",
                      params.toString() );
    }
}
