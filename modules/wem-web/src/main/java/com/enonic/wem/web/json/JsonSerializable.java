package com.enonic.wem.web.json;

import org.codehaus.jackson.JsonNode;

public interface JsonSerializable
{
    public JsonNode toJson();
}
