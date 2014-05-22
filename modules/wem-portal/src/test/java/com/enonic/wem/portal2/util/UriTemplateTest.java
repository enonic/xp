package com.enonic.wem.portal2.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

public class UriTemplateTest
{
    @Test
    public void getVariableNames()
        throws Exception
    {
        final UriTemplate template = new UriTemplate( "http://enonic.com/id/{id}/name/{name}" );
        final List<String> variableNames = template.getVariableNames();
        assertEquals( Arrays.asList( "id", "name" ), variableNames );
    }

    @Test
    public void matches()
    {
        final UriTemplate template = new UriTemplate( "http://enonic.com/id/{id}/name/{name}" );
        assertFalse( template.matches( "" ) );
        assertFalse( template.matches( null ) );
        assertFalse( template.matches( "http://enonic.com/id/name" ) );
        assertTrue( template.matches( "http://enonic.com/id/32/name/test" ) );
    }

    @Test
    public void match()
    {
        final UriTemplate template = new UriTemplate( "http://enonic.com/id/{id}/name/{name}" );
        final Map<String, String> result = template.match( "http://enonic.com/id/32/name/test" );
        assertEquals( ImmutableMap.of( "id", "32", "name", "test" ), result );
    }

    @Test
    public void matchesCustomRegex()
    {
        final UriTemplate template = new UriTemplate( "http://enonic.com/id/{id:\\d+}" );
        assertTrue( template.matches( "http://enonic.com/id/32" ) );
        assertFalse( template.matches( "http://enonic.com/id/abc" ) );
    }

    @Test
    public void matchCustomRegex()
    {
        final UriTemplate template = new UriTemplate( "http://enonic.com/id/{id:\\d+}" );
        final Map<String, String> result = template.match( "http://enonic.com/id/32" );
        assertEquals( ImmutableMap.of( "id", "32" ), result );
    }

    @Test
    public void matchDuplicate()
    {
        final UriTemplate template = new UriTemplate( "/id/{c}/{c}/{c}" );
        final Map<String, String> result = template.match( "/id/32/32/32" );
        assertEquals( ImmutableMap.of( "c", "32" ), result );
    }

    @Test
    public void matchMultipleInOneSegment()
    {
        final UriTemplate template = new UriTemplate( "/{foo}-{bar}" );
        final Map<String, String> result = template.match( "/12-34" );
        assertEquals( ImmutableMap.of( "foo", "12", "bar", "34" ), result );
    }
}
