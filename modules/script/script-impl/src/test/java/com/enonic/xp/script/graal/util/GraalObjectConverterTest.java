package com.enonic.xp.script.graal.util;

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
