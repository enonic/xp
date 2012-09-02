package com.enonic.wem.web.jsonrpc;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.web.json.JsonSerializable;

public interface JsonRpcContext
    extends Iterable<JsonRpcParam>
{
    public JsonNode getResult();

    public JsonRpcParam param( final String name );

    public void setResult( final String value );

    public void setResult( final JsonNode value );

    public void setResult( final JsonSerializable value );
}
