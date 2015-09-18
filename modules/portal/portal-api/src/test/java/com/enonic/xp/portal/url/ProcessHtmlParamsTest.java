package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

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

        final ProcessHtmlParams params = configure( new ProcessHtmlParams() );
        params.setAsMap( map );

        assertEquals( "<html/>", params.getValue() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ProcessHtmlParams{type=server, params={a=[1]}, value=<html/>}", params.toString() );
    }
}
