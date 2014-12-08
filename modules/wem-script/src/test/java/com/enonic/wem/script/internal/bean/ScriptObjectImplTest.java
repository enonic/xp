package com.enonic.wem.script.internal.bean;

import javax.script.Bindings;

import org.junit.Test;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.ScriptObject;
import com.enonic.wem.script.internal.NashornScriptTest;

import static org.junit.Assert.*;

public class ScriptObjectImplTest
    extends NashornScriptTest
{
    @Override
    protected void configure( final Bindings bindings )
    {
    }

    private ScriptObject evalObject( final String script )
        throws Exception
    {
        final Object result = eval( script );
        return new ScriptObjectImpl( result );
    }

    @Test
    public void testUndefined()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = undefined; x" );

        assertEquals( false, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( true, obj.isUndefined() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( null, obj.getKeys() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( null, obj.getArray() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( null, obj.getValue( String.class ) );
    }

    @Test
    public void testValue()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = 1; x" );

        assertEquals( false, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( false, obj.isUndefined() );
        assertEquals( true, obj.isValue() );
        assertEquals( 1, obj.getValue() );
        assertEquals( null, obj.getKeys() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( null, obj.getArray() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( "1", obj.getValue( String.class ) );
    }

    @Test
    public void testArray()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = [1,2]; x" );

        assertEquals( true, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( false, obj.isUndefined() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( null, obj.getKeys() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( 2, obj.getArray().size() );
        assertEquals( 1, obj.getArray().get( 0 ).getValue() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( null, obj.getValue( String.class ) );
    }

    @Test
    public void testObject()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = { a:1, b:2 }; x" );

        assertEquals( false, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( true, obj.isObject() );
        assertEquals( false, obj.isUndefined() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( 2, obj.getKeys().size() );
        assertEquals( true, obj.hasMember( "a" ) );
        assertEquals( 1, obj.getMember( "a" ).getValue() );
        assertEquals( null, obj.getArray() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( null, obj.getValue( String.class ) );
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = function(a) { return a; }; x" );

        assertEquals( false, obj.isArray() );
        assertEquals( true, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( false, obj.isUndefined() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( null, obj.getKeys() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( null, obj.getArray() );
        assertEquals( "a", obj.call( "a" ).getValue() );
        assertEquals( null, obj.getValue( String.class ) );
    }

    @Test
    public void testFunction_error()
        throws Exception
    {
        final ScriptObject obj = evalObject( "var x = function(a) { throw 'error'; }; x" );

        try
        {
            obj.call( "a" ).getValue();
            fail( "Should throw exception" );
        }
        catch ( final Exception e )
        {
            assertTrue( e instanceof ResourceProblemException );
        }
    }
}
