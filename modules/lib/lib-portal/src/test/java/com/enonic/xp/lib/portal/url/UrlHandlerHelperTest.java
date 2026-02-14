package com.enonic.xp.lib.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.script.ScriptValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UrlHandlerHelperTest
{
    @Test
    void testResolveQueryParams_simpleValues()
    {
        final ScriptValue params = mock( ScriptValue.class );
        when( params.getMap() ).thenReturn( Map.of( "a", 1, "b", "test", "c", true ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        assertEquals( "1", result.get( "a" ).iterator().next() );
        assertEquals( "test", result.get( "b" ).iterator().next() );
        assertEquals( "true", result.get( "c" ).iterator().next() );
    }

    @Test
    void testResolveQueryParams_arrayOfSimpleValues()
    {
        final ScriptValue params = mock( ScriptValue.class );
        when( params.getMap() ).thenReturn( Map.of( "a", List.of( 1, 2, 3 ) ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        final Collection<String> values = result.get( "a" );
        assertEquals( 3, values.size() );
        assertEquals( List.of( "1", "2", "3" ), List.copyOf( values ) );
    }

    @Test
    void testResolveQueryParams_nestedObjects()
    {
        final ScriptValue params = mock( ScriptValue.class );
        final List<Map<String, Object>> nestedList = List.of( Map.of( "name", "a" ), Map.of( "name", "b" ) );

        when( params.getMap() ).thenReturn( Map.of( "a", nestedList ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        final Collection<String> values = result.get( "a" );
        assertEquals( 2, values.size() );

        final List<String> valuesList = List.copyOf( values );
        assertEquals( "{\"name\":\"a\"}", valuesList.get( 0 ) );
        assertEquals( "{\"name\":\"b\"}", valuesList.get( 1 ) );
    }

    @Test
    void testResolveQueryParams_complexNestedStructure()
    {
        final ScriptValue params = mock( ScriptValue.class );
        final Map<String, Object> nestedObject = Map.of( "key1", "value1", "key2", 123, "key3", true );

        when( params.getMap() ).thenReturn( Map.of( "complex", nestedObject ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        final String json = result.get( "complex" ).iterator().next();

        // JSON should contain all the keys and values
        assertEquals( "{\"key1\":\"value1\",\"key2\":123,\"key3\":true}", json );
    }

    @Test
    void testResolveQueryParams_arrayOfMixedTypes()
    {
        final ScriptValue params = mock( ScriptValue.class );
        final List<Object> mixedList = List.of( "string", 42, true, Map.of( "nested", "value" ) );

        when( params.getMap() ).thenReturn( Map.of( "mixed", mixedList ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        final Collection<String> values = result.get( "mixed" );
        assertEquals( 4, values.size() );

        final List<String> valuesList = List.copyOf( values );
        assertEquals( "string", valuesList.get( 0 ) );
        assertEquals( "42", valuesList.get( 1 ) );
        assertEquals( "true", valuesList.get( 2 ) );
        assertEquals( "{\"nested\":\"value\"}", valuesList.get( 3 ) );
    }

    @Test
    void testResolveQueryParams_stringEscaping()
    {
        final ScriptValue params = mock( ScriptValue.class );
        final Map<String, Object> nestedObject = Map.of( "key", "value with \"quotes\" and \n newline" );

        when( params.getMap() ).thenReturn( Map.of( "escaped", nestedObject ) );

        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( params );

        assertNotNull( result );
        final String json = result.get( "escaped" ).iterator().next();

        // JSON should have properly escaped characters
        assertEquals( "{\"key\":\"value with \\\"quotes\\\" and \\n newline\"}", json );
    }

    @Test
    void testResolveQueryParams_null()
    {
        final Map<String, Collection<String>> result = UrlHandlerHelper.resolveQueryParams( null );
        assertEquals( null, result );
    }
}
