package com.enonic.wem.web.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public abstract class JsonResult
    implements JsonSerializable
{
    private boolean success;

    private String error;

    public JsonResult()
    {
        this( true );
    }

    public JsonResult( final boolean success )
    {
        this.success = success;
    }

    public final JsonResult error( final String error )
    {
        this.error = error;
        this.success = false;
        return this;
    }

    protected abstract void serialize( ObjectNode json );

    @Override
    public final JsonNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "success", this.success );

        if ( this.error != null )
        {
            json.put( "error", this.error );
        }

        serialize( json );
        return json;
    }

    protected final ObjectNode objectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    protected final ArrayNode arrayNode()
    {
        return JsonNodeFactory.instance.arrayNode();
    }
}
