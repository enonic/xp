package com.enonic.xp.script.impl.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.impl.JSContextFactory;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsObjectConverterTest
{
    private Context context;

    private JsObjectConverter instance;

    @BeforeEach
    public void setUp()
    {
        this.context = JSContextFactory.create();

        JavascriptHelperFactory factory = new JavascriptHelperFactory( this.context );
        this.instance = new JsObjectConverter( factory.create() );
    }

    @AfterEach
    public void destroy()
    {
        this.context.close();
    }

    @Test
    public void testToJs()
    {
        final String[] values = {"value1", "value2", "value3"};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof Value );
        assertTrue( ( (Value) result ).hasArrayElements() );
        assertEquals( 3, ( (Value) result ).getArraySize() );
    }

    @Test
    public void testToJs_Primitives()
    {
        final int[] values = {1, 2, 3};

        final Object result = instance.toJs( values );

        assertTrue( result instanceof int[] );
        assertEquals( 3, ( (int[]) result ).length );
    }

    @Test
    public void testToJS_MapSerializable()
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
