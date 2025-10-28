package com.enonic.xp.script.impl;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ScriptValueTest
    extends AbstractScriptTest
{
    private ScriptValue evalValue( final String name )
    {
        final ScriptExports exports = runTestScript( "value/value-test.js" );
        return exports.executeMethod( name );
    }

    @Test
    void testUndefined()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testUndefined" );
        assertEquals( null, obj );
    }

    @Test
    void testValue()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testValue" );

        assertEquals( false, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( true, obj.isValue() );
        assertEquals( 1, obj.getValue() );
        assertEquals( 0, obj.getKeys().size() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( 0, obj.getArray().size() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( "1", obj.getValue( String.class ) );
        assertEquals( 0, obj.getArray( String.class ).size() );
        assertEquals( 0, obj.getMap().size() );
    }

    @Test
    void testArray()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testArray" );

        assertEquals( true, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( 0, obj.getKeys().size() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( 2, obj.getArray().size() );
        assertEquals( 1, obj.getArray().get( 0 ).getValue() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( null, obj.getValue( String.class ) );
        assertEquals( "[1, 2]", obj.getArray( String.class ).toString() );
        assertEquals( 0, obj.getMap().size() );
    }

    @Test
    void testObject()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testObject" );

        assertEquals( false, obj.isArray() );
        assertEquals( false, obj.isFunction() );
        assertEquals( true, obj.isObject() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( 2, obj.getKeys().size() );
        assertEquals( true, obj.hasMember( "a" ) );
        assertEquals( 1, obj.getMember( "a" ).getValue() );
        assertEquals( 0, obj.getArray().size() );
        assertEquals( null, obj.call( "a" ) );
        assertEquals( null, obj.getValue( String.class ) );
        assertEquals( 0, obj.getArray( String.class ).size() );
        assertEquals( "a=1,b=2",
                      obj.getMap().entrySet().stream().map( e -> e.getKey() + "=" + e.getValue() ).collect( Collectors.joining( "," ) ) );
    }

    @Test
    void testFunction()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testFunction" );

        assertEquals( false, obj.isArray() );
        assertEquals( true, obj.isFunction() );
        assertEquals( false, obj.isObject() );
        assertEquals( false, obj.isValue() );
        assertEquals( null, obj.getValue() );
        assertEquals( 0, obj.getKeys().size() );
        assertEquals( false, obj.hasMember( "a" ) );
        assertEquals( null, obj.getMember( "a" ) );
        assertEquals( 0, obj.getArray().size() );
        assertEquals( "a", obj.call( "a" ).getValue() );
        assertEquals( null, obj.getValue( String.class ) );
        assertEquals( 0, obj.getArray( String.class ).size() );
        assertEquals( 0, obj.getMap().size() );
    }

    @Test
    void testFunction_error()
        throws Exception
    {
        final ScriptValue obj = evalValue( "testFunctionError" );

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
