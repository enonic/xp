package com.enonic.wem.web.json.rpc;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.web.json.JsonSerializable;

public interface JsonRpcContext
    extends Iterable<JsonRpcParam>
{
    public Iterable<JsonRpcParam> params();

    public JsonNode getResult();

    public JsonRpcParam param( final String name );

    public void setResult( final String value );

    public void setResult( final JsonNode value );

    public void setResult( final JsonSerializable value );
}
