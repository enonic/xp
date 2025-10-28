package com.enonic.xp.script.graal.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.impl.util.JavascriptHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraalJavascriptHelperFactoryTest
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
    void testNewObject()
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
    void testNewArray()
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
    void testParseJson()
    {
        Value jsonObject = javascriptHelper.parseJson( "{\"name\": \"Username\", \"age\": 30 }" );

        assertEquals( "Username", jsonObject.getMember( "name" ).asString() );
        assertEquals( 30, jsonObject.getMember( "age" ).asInt() );
    }

    @Test
    void test()
    {
        Value array = javascriptHelper.newJsArray();
        GraalJSHelper.addToNativeArray( array, 1 );
        GraalJSHelper.addToNativeArray( array, 2 );
        assertEquals( 1, array.getArrayElement( 0 ).asInt() );
        assertEquals( 2, array.getArrayElement( 1 ).asInt() );
    }
}
