package com.enonic.xp.util;

import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class StringTemplateTest
{
    @Test
    public void applyNone()
    {
        final Map<String, String> model = Maps.newHashMap();
        final StringTemplate template = new StringTemplate( "{{this}} is a {{test}}" );
        final String value = template.apply( model );

        assertEquals( "{{this}} is a {{test}}", value );
    }

    @Test
    public void applyModel()
    {
        final Map<String, String> model = Maps.newHashMap();
        model.put( "this", "This" );
        model.put( "test", "TEST" );

        final StringTemplate template = new StringTemplate( "{{this}} is a {{test}}" );
        final String value = template.apply( model );

        assertEquals( "This is a TEST", value );
    }

    @Test
    public void applyModelEscape()
    {
        final Map<String, String> model = Maps.newHashMap();
        model.put( "this", "This" );
        model.put( "test", "TEST" );

        final StringTemplate template = new StringTemplate( "{{this}} is a \\{{test}}" );
        final String value = template.apply( model );

        assertEquals( "This is a {{test}}", value );
    }

    @Test
    public void loadFromResource()
    {
        final Map<String, String> model = Maps.newHashMap();
        model.put( "mode", "file" );
        model.put( "one", "1" );
        model.put( "two", "2" );

        final StringTemplate template = StringTemplate.load( getClass(), getClass().getSimpleName() + ".txt" );
        final String value = template.apply( model );

        assertEquals( "This is a file test.\n" + "1, {{two}}, 3\n", normalizeString( value ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadFromResource_notFound()
    {
        StringTemplate.load( getClass(), "unknown.txt" );
    }

    private String normalizeString( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }
}
