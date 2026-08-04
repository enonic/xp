package com.enonic.xp.script.impl.util;

import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsObjectConverterTest
{
    private ScriptEngine engine;

    private JsObjectConverter instance;

    @BeforeEach
    void setUp()
    {
        this.engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( this.engine );

        final JavascriptHelper javascriptHelper = factory.create();

        this.instance = new JsObjectConverter( javascriptHelper );
    }

    @Test
    void testToJs()
    {
        final String[] values = {"value1", "value2", "value3"};

        final Object result = instance.toJs( values );

        assertTrue( NashornHelper.isNativeArray( result ) );
        assertEquals( 3, ( (Bindings) result ).size() );
    }

    @Test
    void testToJs_Primitives()
    {
        final int[] values = {1, 2, 3};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof int[] );
        assertEquals( 3, ( (int[]) result ).length );
    }

    @Test
    @SuppressWarnings("unchecked")
    void fromJs_keepsNullValuedKeys()
        throws Exception
    {
        final Object source = this.engine.eval( "({a: null, b: 1})" );

        final Map<String, Object> result = (Map<String, Object>) instance.fromJs( source );

        // {key: null} is not {} — the key survives with a null value, identically on GraalJS
        assertEquals( 2, result.size() );
        assertTrue( result.containsKey( "a" ) );
        assertNull( result.get( "a" ) );
        assertEquals( 1, ( (Number) result.get( "b" ) ).intValue() );
    }

    @Test
    @SuppressWarnings("unchecked")
    void fromJs_dropsNullArrayElements()
        throws Exception
    {
        final Object source = this.engine.eval( "([1, null, 2])" );

        final List<Object> result = (List<Object>) instance.fromJs( source );

        // list conversion skips null elements, identically on GraalJS
        assertEquals( 2, result.size() );
    }
}
