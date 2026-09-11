package com.enonic.xp.script.graal.util;

import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalObjectConverterTest
{
    private Context context;

    private ObjectConverter instance;

    @BeforeEach
    void setUp()
    {
        this.context = new GraalJSContextFactory().create();

        GraalJavascriptHelperFactory factory = new GraalJavascriptHelperFactory();
        this.instance = factory.create( this.context ).objectConverter();
    }

    @AfterEach
    void destroy()
    {
        this.context.close();
    }

    @Test
    void testToJs()
    {
        final String[] values = {"value1", "value2", "value3"};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof Value );
        assertTrue( ( (Value) result ).hasArrayElements() );
        assertEquals( 3, ( (Value) result ).getArraySize() );
    }

    @Test
    void testToJs_protoKeyDoesNotChangePrototype()
    {
        final Value result = (Value) instance.toJs( Map.of( "__proto__", List.of( "a", "b" ) ) );

        final Value check = context.eval( "js", "(o) => Object.getPrototypeOf(o) === Object.prototype" +
            " && Array.isArray(o.__proto__) && o.__proto__.length === 2 && o.length === undefined" );
        assertTrue( check.execute( result ).asBoolean() );
    }

    @Test
    void testToJs_Primitives()
    {
        final int[] values = {1, 2, 3};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof int[] );
        assertEquals( 3, ( (int[]) result ).length );
    }

    @Test
    void testToJS_MapSerializable()
    {
        Object result = instance.toJs( new SimpleMapSerializer() );
        Value bindings = this.context.getBindings( "js" );
        Value jsonProto = bindings.getMember( "JSON" );

        assertEquals( "{\"nodes\":[{\"id\":1,\"name\":\"name\"}]}", jsonProto.getMember( "stringify" ).execute( result ).asString() );
    }

    @Test
    void toJs_map()
    {
        final Object result = instance.toJs( Map.of( "outer", Map.of( "inner", List.of( 1, 2 ) ) ) );

        // a script reads members, so the map has to arrive as an object and not as a host value
        final Value bindings = this.context.getBindings( "js" );
        assertEquals( "{\"outer\":{\"inner\":[1,2]}}",
                      bindings.getMember( "JSON" ).getMember( "stringify" ).execute( result ).asString() );
    }

    @Test
    @SuppressWarnings("unchecked")
    void fromJs_keepsNullValuedKeys()
    {
        final Value source = this.context.eval( "js", "({a: null, b: 1, c: undefined})" );

        final Map<String, Object> result = (Map<String, Object>) instance.fromJs( source );

        // parity with Nashorn: {key: null} is not {} — the key survives with a null value;
        // undefined is indistinguishable from null across the polyglot boundary
        assertEquals( 3, result.size() );
        assertTrue( result.containsKey( "a" ) );
        assertNull( result.get( "a" ) );
        assertEquals( 1, ( (Number) result.get( "b" ) ).intValue() );
        assertTrue( result.containsKey( "c" ) );
        assertNull( result.get( "c" ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    void fromJs_dropsNullArrayElements()
    {
        final Value source = this.context.eval( "js", "([1, null, 2])" );

        final List<Object> result = (List<Object>) instance.fromJs( source );

        // parity with Nashorn: list conversion skips null elements
        assertEquals( 2, result.size() );
    }

    private static class SimpleMapSerializer
        implements MapSerializable
    {

        @Override
        public void serialize( final MapGenerator gen )
        {
            gen.array( "nodes" );
            gen.map();
            gen.value( "id", 1 );
            gen.value( "name", "name" );
            gen.end();
            gen.end();
        }
    }
}
