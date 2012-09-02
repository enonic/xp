package com.enonic.wem.web.json.result;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonSerializable;

public abstract class JsonDataResult
    implements JsonSerializable
{
    private final boolean success;

    public JsonDataResult( final boolean success )
    {
        this.success = success;
    }

    protected abstract void serialize( ObjectNode json );

    @Override
    public final JsonNode toJson()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "success", this.success );
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
