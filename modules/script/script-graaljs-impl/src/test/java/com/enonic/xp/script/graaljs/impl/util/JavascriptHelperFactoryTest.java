package com.enonic.xp.script.graaljs.impl.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graaljs.impl.GraalJSContextProviderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavascriptHelperFactoryTest
{
    private Context context;

    private JavascriptHelper javascriptHelper;

    @BeforeEach
    public void setUp()
    {
        this.context = new GraalJSContextProviderImpl().getContext();

        JavascriptHelperFactory instance = new JavascriptHelperFactory( context );
        this.javascriptHelper = instance.create();
    }

    @AfterEach
    public void destroy()
    {
        this.context.close();
    }

    @Test
    public void testNewObject()
    {
        Value config = javascriptHelper.newJsObject();
        config.putMember( "prop1", Value.asValue( "value1" ) );

        Value info = javascriptHelper.newJsObject();
        info.putMember( "name", Value.asValue( "app" ) );
        info.putMember( "version", Value.asValue( "1.0.0" ) );
        info.putMember( "config", config );

        assertEquals( "app", info.getMember( "name" ).asString() );
        assertEquals( "1.0.0", info.getMember( "version" ).asString() );
        assertEquals( "value1", info.getMember( "config" ).getMember( "prop1" ).asString() );
    }

    @Test
    public void testNewArray()
    {
        Value array = javascriptHelper.newJsArray();
        array.getMember( "push" ).execute( Value.asValue( "1" ) );
        array.getMember( "push" ).execute( Value.asValue( "2" ) );
        array.getMember( "push" ).execute( Value.asValue( "3" ) );

        assertEquals( 3, array.getArraySize() );
        assertEquals( "1", array.getArrayElement( 0 ).asString() );
        assertEquals( "2", array.getArrayElement( 1 ).asString() );
        assertEquals( "3", array.getArrayElement( 2 ).asString() );
    }

    @Test
    public void testParseJson()
    {
        Value jsonObject = javascriptHelper.parseJson( "{\"name\": \"Username\", \"age\": 30 }" );

        assertEquals( "Username", jsonObject.getMember( "name" ).asString() );
        assertEquals( 30, jsonObject.getMember( "age" ).asInt() );
    }

    @Test
    public void test()
    {
        Value array = javascriptHelper.newJsArray();
        GraalJSHelper.addToNativeArray( array, 1 );
        GraalJSHelper.addToNativeArray( array, 2 );
        assertEquals( 1, array.getArrayElement( 0 ).asInt() );
        assertEquals( 2, array.getArrayElement( 1 ).asInt() );
    }

    @Test
    public void testScriptMapGenerator()
    {
        Value value = context.eval( "js", "var array = [1, 2, 3]; array;" );

        ScriptMapGenerator generator = new ScriptMapGenerator( javascriptHelper );
        assertFalse( generator.isMap( value ) );

        value = context.eval( "js", "var obj = { key: 1 }; obj;" );
        assertTrue( generator.isMap( value ) );

        value = context.eval( "js", "var count = 1; count;" );
        assertFalse( generator.isMap( value ) );

        value = context.eval( "js", "var count = undefined; count;" );
        assertFalse( generator.isMap( value ) );
    }
}
