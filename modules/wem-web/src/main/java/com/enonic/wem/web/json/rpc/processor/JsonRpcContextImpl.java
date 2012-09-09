package com.enonic.wem.web.json.rpc.processor;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.Maps;

import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcParam;

final class JsonRpcContextImpl
    implements JsonRpcContext
{
    private final Map<String, JsonRpcParam> params;

    private JsonNode result;

    public JsonRpcContextImpl( final ObjectNode params )
    {
        this.params = Maps.newHashMap();

        if ( params != null )
        {
            setParams( params );
        }
    }

    private void setParams( final ObjectNode json )
    {
        final Iterator<String> it = json.getFieldNames();
        while ( it.hasNext() )
        {
            final String name = it.next();
            this.params.put( name, JsonRpcParamImpl.create( name, json.get( name ) ) );
        }
    }

    @Override
    public JsonNode getResult()
    {
        return this.result;
    }

    @Override
    public JsonRpcParam param( final String name )
    {
        final JsonRpcParam param = this.params.get( name );
        if ( param != null )
        {
            return param;
        }
        else
        {
            return JsonRpcParamImpl.create( name, null );
        }
    }

    @Override
    public Iterator<JsonRpcParam> iterator()
    {
        return this.params.values().iterator();
    }

    @Override
    public void setResult( final String value )
    {
        if (value != null) {
            setResult( JsonNodeFactory.instance.textNode( value ) );
        }
    }

    @Override
    public void setResult( final JsonNode value )
    {
        this.result = value;
    }

    @Override
    public void setResult( final JsonSerializable value )
    {
        if (value != null) {
            setResult( value.toJson() );
        }
    }
}
