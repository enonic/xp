package com.enonic.xp.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpolatorTest
{
    @Test
    void recursive()
    {
        final Map<String, String> model = new HashMap<>();
        model.put( "this", "This" );
        model.put( "test", "TEST" );
        model.put( "testRecursive", "recursive${test}" );

        final String value = Interpolator.classic().interpolate( "${this} is a ${testRecursive}", model::get );

        assertEquals( "This is a recursiveTEST", value );
    }

    @Test
    void escape()
    {
        final Map<String, String> model = new HashMap<>();
        model.put( "this", "This" );
        model.put( "test", "TEST" );
        model.put( "test2", "TEST2" );

        final String value = Interpolator.classic().interpolate( "${this} is a $${test} $$$${test2}", model::get );

        assertEquals( "This is a ${test} $$${test2}", value );
    }

    @Test
    void recursive_2()
    {
        final Map<String, String> model = new HashMap<>();
        model.put( "this", "This" );
        model.put( "test", "TEST" );
        model.put( "testRecursive", "recursive{{test}}" );

        final String value = new Interpolator( "{{", "}}", '\\' ).interpolate( "{{this}} is a {{testRecursive}}", model::get );

        assertEquals( "This is a recursiveTEST", value );
    }

    @Test
    void escape_2()
    {
        final Map<String, String> model = new HashMap<>();
        model.put( "this", "This" );
        model.put( "test", "TEST" );
        model.put( "test2", "TEST2" );

        final String value = new Interpolator( "{{", "}}", '\\' ).interpolate( "{{this}} is a \\{{test}} \\\\\\{{test2}}", model::get );

        assertEquals( "This is a {{test}} \\\\{{test2}}", value );
    }
}
