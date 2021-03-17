package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProcessHtmlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testValue()
    {
        final ProcessHtmlParams params = configure( new ProcessHtmlParams() );
        assertNull( params.getValue() );

        params.value( "" );
        assertNull( params.getValue() );

        params.value( "<html/>" );
        assertEquals( "<html/>", params.getValue() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_value", "<html/>" );
        map.put( "a", "1" );
        map.put( "_imageWidths", "600" );
        map.put( "_imageWidths", "1024" );

        final ProcessHtmlParams params = configure( new ProcessHtmlParams() );
        params.setAsMap( map );

        assertEquals( "<html/>", params.getValue() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ProcessHtmlParams{type=server, params={a=[1]}, value=<html/>, imageWidths=[600, 1024]}", params.toString() );
    }
}
