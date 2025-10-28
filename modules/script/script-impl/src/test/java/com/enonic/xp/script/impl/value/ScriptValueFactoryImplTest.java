package com.enonic.xp.script.impl.value;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.util.NashornHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptValueFactoryImplTest
{
    private ScriptValueFactory factory;

    private ScriptEngine engine;

    @BeforeEach
    void setup()
    {
        this.engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( this.engine );
        this.factory = new ScriptValueFactoryImpl( factory.create() );
    }

    @Test
    void newValue_null()
    {
        final ScriptValue value = this.factory.newValue( null );
        assertNull( value );
    }

    @Test
    void newValue_scalar()
    {
        final ScriptValue value = this.factory.newValue( "2" );

        assertNotNull( value );
        assertEquals( false, value.isArray() );
        assertEquals( false, value.isFunction() );
        assertEquals( false, value.isObject() );
        assertEquals( true, value.isValue() );

        assertEquals( "2", value.getValue() );
        assertEquals( 2, value.getValue( Integer.class ) );
        assertTrue( value.getList().isEmpty() );

        assertNonArray( value );
        assertNonObject( value );
        assertNonFunction( value );
    }

    @Test
    void newValue_function()
    {
        final Object obj = execute( "var result = function() {}; result;" );

        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertEquals( false, value.isArray() );
        assertEquals( true, value.isFunction() );
        assertEquals( false, value.isObject() );
        assertEquals( false, value.isValue() );

        assertNonValue( value );
        assertNonArray( value );
        assertNonObject( value );
    }

    @Test
    void newValue_array()
    {
        final Object obj = execute( "var result = ['1', '2']; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertEquals( true, value.isArray() );
        assertEquals( false, value.isFunction() );
        assertEquals( false, value.isObject() );
        assertEquals( false, value.isValue() );

        assertNonValue( value );
        assertNonObject( value );
        assertNonFunction( value );

        assertNotNull( value.getArray() );
        assertEquals( 2, value.getArray().size() );
        assertNotNull( value.getArray( Integer.class ) );
        assertEquals( 2, value.getArray( Integer.class ).size() );
        assertEquals( "1", value.getArray().get( 0 ).getValue() );
        assertEquals( "2", value.getArray().get( 1 ).getValue() );
        assertEquals( 1, value.getArray( Integer.class ).get( 0 ) );
        assertEquals( 2, value.getArray( Integer.class ).get( 1 ) );
        assertEquals( 2, value.getList().size() );
        assertEquals( "1", value.getList().get( 0 ) );
        assertEquals( "2", value.getList().get( 1 ) );
    }

    @Test
    void array_toList()
    {
        final Object obj = execute( "var result = ['1', null, 2, {'key': 'value'}, [42]]; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertTrue( value.isArray() );

        assertNonValue( value );
        assertNonObject( value );
        assertNonFunction( value );

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
    void newValue_object()
    {
        final Object obj = execute( "var result = {'a':1, 'b':2}; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        assertNotNull( value );
        assertEquals( false, value.isArray() );
        assertEquals( false, value.isFunction() );
        assertEquals( true, value.isObject() );
        assertEquals( false, value.isValue() );

        assertNonValue( value );
        assertNonArray( value );
        assertNonFunction( value );

        assertNotNull( value.getKeys() );
        assertEquals( 2, value.getKeys().size() );
        assertEquals( "a,b", String.join( ",", value.getKeys() ) );

        assertNotNull( value.getMap() );
        assertEquals( 2, value.getMap().size() );
        assertNotNull( value.getMember( "a" ) );
        assertEquals( 1, value.getMember( "a" ).getValue() );
        assertTrue( value.hasMember( "a" ) );
    }

    private void assertNonValue( final ScriptValue value )
    {
        assertNull( value.getValue() );
        assertNull( value.getValue( Integer.class ) );
    }

    private void assertNonArray( final ScriptValue value )
    {
        assertNotNull( value.getArray() );
        assertEquals( 0, value.getArray().size() );
        assertNotNull( value.getArray( Integer.class ) );
        assertEquals( 0, value.getArray( Integer.class ).size() );
    }

    private void assertNonObject( final ScriptValue value )
    {
        assertNotNull( value.getKeys() );
        assertEquals( 0, value.getKeys().size() );

        assertNotNull( value.getMap() );
        assertEquals( 0, value.getMap().size() );
        assertNull( value.getMember( "test" ) );
        assertFalse( value.hasMember( "test" ) );
    }

    private void assertNonFunction( final ScriptValue value )
    {
        assertNull( value.call( "a", "b" ) );
    }

    private Object execute( final String script )
    {
        try
        {
            return this.engine.eval( script );
        }
        catch ( ScriptException e )
        {
            throw new RuntimeException(e);
        }
    }
}
