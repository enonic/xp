package com.enonic.xp.admin.impl.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonSerializable
{
    public JsonNode toJson();
}
