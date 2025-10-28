package com.enonic.xp.script.graal.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.impl.util.JavascriptHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalScriptMapGeneratorTest
{
    private Context context;

    private JavascriptHelper<Value> javascriptHelper;

    @BeforeEach
    void setUp()
    {
        this.context =new GraalJSContextFactory().create();

        GraalJavascriptHelperFactory instance = new GraalJavascriptHelperFactory();
        this.javascriptHelper = instance.create(context);
    }

    @AfterEach
    void destroy()
    {
        this.context.close();
    }

    @Test
    void testScriptMapGenerator()
    {
        Value value = context.eval( "js", "var array = [1, 2, 3]; array;" );

        GraalScriptMapGenerator generator = new GraalScriptMapGenerator( javascriptHelper );
        assertFalse( generator.isMap( value ) );

        value = context.eval( "js", "var obj = { key: 1 }; obj;" );
        assertTrue( generator.isMap( value ) );

        value = context.eval( "js", "var count = 1; count;" );
        assertFalse( generator.isMap( value ) );

        value = context.eval( "js", "var count = undefined; count;" );
        assertFalse( generator.isMap( value ) );
    }
}
