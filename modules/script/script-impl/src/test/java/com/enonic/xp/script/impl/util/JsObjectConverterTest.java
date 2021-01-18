package com.enonic.xp.script.impl.util;

import javax.script.ScriptEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsObjectConverterTest
{
    private JsObjectConverter instance;

    @BeforeEach
    public void setUp()
    {
        final ScriptEngine engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( engine );

        final JavascriptHelper javascriptHelper = factory.create();

        this.instance = new JsObjectConverter( javascriptHelper );
    }

    @Test
    public void testToJs()
    {
        final String[] values = {"value1", "value2", "value3"};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof ScriptObjectMirror );
        assertTrue( ( (ScriptObjectMirror) result ).isArray() );
        assertEquals( 3, ( (ScriptObjectMirror) result ).size() );
    }

    @Test
    public void testToJs_Primitives()
    {
        final int[] values = {1, 2, 3};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof int[] );
        assertEquals( 3, ( (int[]) result ).length );
    }
}
