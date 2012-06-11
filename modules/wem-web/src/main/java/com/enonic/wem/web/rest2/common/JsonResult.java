package com.enonic.wem.web.rest2.common;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public abstract class JsonResult
{
    protected final ObjectNode objectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    public abstract JsonNode toJson();
}
