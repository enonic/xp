package com.enonic.wem.web.old.common;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonSerializable;

@Deprecated
public abstract class JsonResult
    implements JsonSerializable
{
    public static String TOTAL = "total";

    protected final ObjectNode objectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    protected final ArrayNode arrayNode()
    {
        return JsonNodeFactory.instance.arrayNode();
    }
}
