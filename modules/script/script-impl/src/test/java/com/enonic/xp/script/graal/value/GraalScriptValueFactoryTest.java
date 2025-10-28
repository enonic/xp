package com.enonic.xp.script.graal.value;

import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalScriptValueFactoryTest
{
    private Context context;

    private ScriptValueFactory<Value> factory;

    @BeforeEach
    void setUp()
    {
        final GraalScriptValueFactory factory = new GraalScriptValueFactory( new GraalJSContextFactory(), new GraalJavascriptHelperFactory() );
        this.factory = factory;
        this.context = factory.getContext();
    }

    @AfterEach
    void destroy()
    {
        context.close();
    }

    @Test
    void test()
    {
        ScriptValue value = factory.newValue( "2" );
        assertNotNull( value );
        assertTrue( value.isValue() );
        assertEquals( "2", value.getValue() );

        Value arrayValue = context.eval( "js", "var array = [1,2,3]; array;" );
        ScriptValue scriptValue = factory.newValue( arrayValue );

        assertNotNull( scriptValue );
        assertTrue( scriptValue.isArray() );
        final List<Object> list = scriptValue.getList();
        assertEquals( 3, list.size() );
        assertEquals( 1, list.get( 0 ) );
        assertEquals( 2, list.get( 1 ) );
        assertEquals( 3, list.get( 2 ) );
    }

    @Test
    void test_newValue_function()
    {
        Value funcValue = context.eval( "js", "var func = function() { return 1; }; func;" );
        ScriptValue funcScriptValue = factory.newValue( funcValue );

        assertNotNull( funcScriptValue );
        assertTrue( funcScriptValue.isFunction() );

        ScriptValue result = funcScriptValue.call();
        assertEquals( 1, result.getValue(Integer.class) );
    }

    @Test
    void test_newValue_array_toList()
    {
        final Object obj = context.eval( "js", "var result = ['1', null, 2, {'key': 'value'}, [42]]; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertTrue( value.isArray() );

        final List<Object> list = value.getList();
        assertNotNull( list );
        assertEquals( 5, list.size() );
        assertEquals( "1", list.get( 0 ) );
        assertNull( list.get( 1 ) );
        assertEquals( 2, list.get( 2 ) );
        assertTrue( list.get( 3 ) instanceof Map );
        assertTrue( list.get( 4 ) instanceof List );
    }

    @Test
    void test_newValue_object()
    {
        final Object obj = context.eval( "js", "var result = {'a':1, 'b':2, 'c': false, 'd': 2.1}; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertFalse( value.isArray() );
        assertFalse( value.isFunction() );
        assertFalse( value.isValue() );
        assertTrue( value.isObject() );

        assertNotNull( value.getKeys() );
        assertEquals( 4, value.getKeys().size() );
        assertEquals( "a,b,c,d", String.join( ",", value.getKeys() ) );

        assertNotNull( value.getMap() );
        assertEquals( 4, value.getMap().size() );
        assertNotNull( value.getMember( "a" ) );
        assertEquals( 1, value.getMember( "a" ).getValue(Integer.class) );
        assertTrue( value.hasMember( "a" ) );

        assertFalse( value.getMember( "c" ).getValue( Boolean.class ) );
        assertFalse( (boolean) value.getMember( "c" ).getValue() );

        assertEquals( 2.1, value.getMember( "d" ).getValue( Double.class ) );
        assertEquals( 2.1, value.getMember( "d" ).getValue() );
    }
}
