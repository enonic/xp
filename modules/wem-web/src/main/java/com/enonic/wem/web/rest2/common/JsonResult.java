package com.enonic.wem.web.rest2.common;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonSerializable;

public abstract class JsonResult
    implements JsonSerializable
{
    protected final ObjectNode objectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    protected final ArrayNode arrayNode()
    {
        return JsonNodeFactory.instance.arrayNode();
    }
}
