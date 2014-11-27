package com.enonic.wem.script.internal.serializer;

import java.util.Stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.script.serializer.MapGenerator;

public final class JsonMapGenerator
    implements MapGenerator
{
    private final ObjectNode root;

    private JsonNode current;

    private Stack<JsonNode> stack;

    private String name;

    public JsonMapGenerator()
    {
        this.root = newMap();
        this.stack = new Stack<>();
        this.current = this.root;
    }

    private ObjectNode newMap()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    private ArrayNode newArray()
    {
        return JsonNodeFactory.instance.arrayNode();
    }

    public ObjectNode getRootObject()
    {
        return this.root;
    }

    private String popName()
    {
        final String name = this.name;
        if ( name != null )
        {
            this.name = null;
            return name;
        }

        throw new IllegalStateException( "Name must be set" );
    }

    @Override
    public MapGenerator map()
    {
        this.stack.push( this.current );
        if ( this.current instanceof ObjectNode )
        {
            this.current = ( (ObjectNode) this.current ).putObject( popName() );
        }
        else if ( this.current instanceof ArrayNode )
        {
            this.current = ( (ArrayNode) this.current ).addObject();
        }

        return this;
    }

    @Override
    public MapGenerator array()
    {
        this.stack.push( this.current );
        if ( this.current instanceof ObjectNode )
        {
            this.current = ( (ObjectNode) this.current ).putArray( popName() );
        }
        else if ( this.current instanceof ArrayNode )
        {
            this.current = ( (ArrayNode) this.current ).addArray();
        }

        return this;
    }

    @Override
    public MapGenerator end()
    {
        this.current = this.stack.pop();
        return this;
    }

    @Override
    public MapGenerator name( final String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public MapGenerator value( final Object value )
    {
        if ( this.current instanceof ObjectNode )
        {
            ( (ObjectNode) this.current ).put( popName(), value.toString() );
        }
        else if ( this.current instanceof ArrayNode )
        {
            ( (ArrayNode) this.current ).add( value.toString() );
        }

        return this;
    }
}
