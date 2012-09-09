package com.enonic.wem.web.json.rpc.processor;

import java.lang.reflect.Array;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.json.rpc.JsonRpcError;
import com.enonic.wem.web.json.rpc.JsonRpcParam;

final class JsonRpcParamImpl
    implements JsonRpcParam
{
    private final String name;

    private final JsonNode[] values;

    private JsonRpcParamImpl( final String name, final JsonNode[] values )
    {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean isNull()
    {
        return this.values == null;
    }

    @Override
    public JsonRpcParam required()
        throws JsonRpcException
    {
        if ( isNull() )
        {
            final JsonRpcError error = JsonRpcError.invalidParams( "Parameter [" + this.name + "] is required" );
            throw new JsonRpcException( error );
        }

        return this;
    }

    private <T> T getValue( final T[] values, final T defValue )
    {
        if ( values == null )
        {
            return defValue;
        }

        if ( values.length == 0 )
        {
            return defValue;
        }

        if ( values[0] == null )
        {
            return defValue;
        }

        return values[0];
    }

    @Override
    public String asString()
    {
        return asString( null );
    }

    @Override
    public String asString( final String defValue )
    {
        return getValue( asStringArray(), defValue );
    }

    @Override
    public String[] asStringArray()
    {
        return convert( String.class, new Function<JsonNode, String>()
        {
            @Override
            public String apply( final JsonNode json )
            {
                return json.asText();
            }
        } );
    }

    @Override
    public Integer asInteger()
    {
        return asInteger( null );
    }

    @Override
    public Integer asInteger( final Integer defValue )
    {
        return getValue( asIntegerArray(), defValue );
    }

    @Override
    public Integer[] asIntegerArray()
    {
        return convert( Integer.class, new Function<JsonNode, Integer>()
        {
            @Override
            public Integer apply( final JsonNode json )
            {
                return json.asInt();
            }
        } );
    }

    @Override
    public Double asDouble()
    {
        return asDouble( null );
    }

    @Override
    public Double asDouble( final Double defValue )
    {
        return getValue( asDoubleArray(), defValue );
    }

    @Override
    public Double[] asDoubleArray()
    {
        return convert( Double.class, new Function<JsonNode, Double>()
        {
            @Override
            public Double apply( final JsonNode json )
            {
                return json.asDouble();
            }
        } );
    }

    @Override
    public Boolean asBoolean()
    {
        return asBoolean( null );
    }

    @Override
    public Boolean asBoolean( final Boolean defValue )
    {
        return getValue( asBooleanArray(), defValue );
    }

    @Override
    public Boolean[] asBooleanArray()
    {
        return convert( Boolean.class, new Function<JsonNode, Boolean>()
        {
            @Override
            public Boolean apply( final JsonNode json )
            {
                return json.asBoolean();
            }
        } );
    }

    @Override
    public ObjectNode asObject()
    {
        return asObject( null );
    }

    @Override
    public ObjectNode asObject( final ObjectNode defValue )
    {
        return getValue( asObjectArray(), defValue );
    }

    @Override
    public ObjectNode[] asObjectArray()
    {
        return convert( ObjectNode.class, new Function<JsonNode, ObjectNode>()
        {
            @Override
            public ObjectNode apply( final JsonNode json )
            {
                if ( json instanceof ObjectNode )
                {
                    return (ObjectNode) json;
                }
                else
                {
                    return null;
                }
            }
        } );
    }

    @SuppressWarnings("unchecked")
    private <T> T[] newArray( final Class<T> type, final int size )
    {
        return (T[]) Array.newInstance( type, size );
    }

    private <T> T[] convert( final Class<T> type, final Function<JsonNode, T> function )
    {
        if ( this.values == null )
        {
            return newArray( type, 0 );
        }

        final T[] result = newArray( type, this.values.length );
        for ( int i = 0; i < result.length; i++ )
        {
            result[i] = function.apply( this.values[i] );
        }

        return result;
    }

    public static JsonRpcParamImpl create( final String name, final JsonNode json )
    {
        final JsonNode[] array = toArray( json );
        return new JsonRpcParamImpl( name, array );
    }

    private static JsonNode[] toArray( final JsonNode json )
    {
        if ( json == null )
        {
            return null;
        }

        if ( json instanceof ArrayNode )
        {
            return toArray( (ArrayNode) json );
        }

        return new JsonNode[]{json};
    }

    private static JsonNode[] toArray( final ArrayNode json )
    {
        final ImmutableList<JsonNode> list = ImmutableList.copyOf( json.iterator() );
        return list.toArray( new JsonNode[list.size()] );
    }
}
