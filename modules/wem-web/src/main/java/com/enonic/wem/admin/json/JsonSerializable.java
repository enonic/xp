package com.enonic.wem.admin.json;

import org.codehaus.jackson.JsonNode;

public interface JsonSerializable
{
    public JsonNode toJson();
}
