package com.enonic.xp.script.impl.util;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsObjectConverterTest
{
    private JsObjectConverter instance;

    @BeforeEach
    void setUp()
    {
        final ScriptEngine engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( engine );

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
}
