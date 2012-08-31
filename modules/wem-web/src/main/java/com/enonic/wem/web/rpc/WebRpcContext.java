package com.enonic.wem.web.rpc;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.web.json.JsonSerializable;

public interface WebRpcContext
    extends Iterable<WebRpcParam>
{
    public JsonNode getResult();

    public WebRpcParam param( final String name );

    public void setResult( final String value );

    public void setResult( final JsonNode value );

    public void setResult( final JsonSerializable value );
}
