package com.enonic.xp.script.graal.util;

import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.impl.util.JavascriptHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalJSHelperTest
{
    private Context context;

    private JavascriptHelper javascriptHelper;

    @BeforeEach
    void setUp()
    {
        this.context = new GraalJSContextFactory().create();
        this.javascriptHelper = new GraalJavascriptHelperFactory().create( context );
    }

    @AfterEach
    void destroy()
    {
        this.context.close();
    }

    @Test
    void testDate()
    {
        Value date = context.eval( "js", "var date = new Date(); date;" );
        assertTrue( GraalJSHelper.isDateType( date ) );
        assertNotNull( GraalJSHelper.toDate( date ) );
    }

    @Test
    void testIsUndefined()
    {
        assertTrue( GraalJSHelper.isUndefined( null ) );
        assertTrue( GraalJSHelper.isUndefined( Value.asValue( null ) ) );
        assertFalse( GraalJSHelper.isUndefined( Value.asValue( 1 ) ) );
    }

    @Test
    void testIsNativeArray()
    {
        Value arrayValue = context.eval( "js", "var array = [1, 2, 3]; array;" );
        assertTrue( GraalJSHelper.isNativeArray( arrayValue ) );

        Value nonArrayValue = context.eval( "js", "var count = 100; count;" );
        assertFalse( GraalJSHelper.isNativeArray( nonArrayValue ) );
    }

    @Test
    void testIsNativeObject()
    {
        assertFalse( GraalJSHelper.isNativeObject( 1 ) );
        assertFalse( GraalJSHelper.isNativeObject( 1.0 ) );
        assertFalse( GraalJSHelper.isNativeObject( "string" ) );
        assertFalse( GraalJSHelper.isNativeObject( true ) );
        assertFalse( GraalJSHelper.isNativeObject( javascriptHelper.newJsArray() ) );
        assertTrue( GraalJSHelper.isNativeObject( javascriptHelper.newJsObject() ) );
        assertTrue( GraalJSHelper.isNativeObject( javascriptHelper.parseJson( "{ \"key\": \"value\"}" ) ) );
        assertFalse( GraalJSHelper.isNativeObject( ProxyObject.fromMap( Map.of( "key", "value" ) ) ) );

        final Value asValue = Value.asValue( Map.of() );
        assertTrue( asValue.isHostObject() );
        assertFalse( GraalJSHelper.isNativeObject( asValue ) );
    }
}
