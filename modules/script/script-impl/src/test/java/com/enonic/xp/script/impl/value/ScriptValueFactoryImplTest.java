package com.enonic.xp.script.impl.value;

import javax.script.ScriptEngine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Joiner;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.util.NashornHelper;

import static org.junit.Assert.*;

public class ScriptValueFactoryImplTest
{
    private ScriptValueFactory factory;

    private ScriptEngine engine;

    @Before
    public void setup()
    {
        this.engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( this.engine );
        this.factory = new ScriptValueFactoryImpl( factory.create() );
    }

    @Test
    public void newValue_null()
    {
        final ScriptValue value = this.factory.newValue( null );
        assertNull( value );
    }

    @Test
    public void newValue_scalar()
    {
        final ScriptValue value = this.factory.newValue( "2" );

        assertNotNull( value );
        assertEquals( false, value.isArray() );
        assertEquals( false, value.isFunction() );
        assertEquals( false, value.isObject() );
        assertEquals( true, value.isValue() );

        assertEquals( "2", value.getValue() );
        assertEquals( new Integer( 2 ), value.getValue( Integer.class ) );

        assertNonArray( value );
        assertNonObject( value );
        assertNonFunction( value );
    }

    @Test
    public void newValue_function()
    {
        final JSObject obj = Mockito.mock( JSObject.class );
        Mockito.when( obj.isFunction() ).thenReturn( true );

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
    public void newValue_array()
        throws Exception
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
        assertEquals( new Integer( 1 ), value.getArray( Integer.class ).get( 0 ) );
        assertEquals( new Integer( 2 ), value.getArray( Integer.class ).get( 1 ) );
    }

    @Test
    public void newValue_object()
        throws Exception
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
        assertEquals( "a,b", Joiner.on( "," ).join( value.getKeys() ) );

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
        throws Exception
    {
        return this.engine.eval( script );
    }
}
