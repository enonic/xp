package com.enonic.wem.admin.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonSerializable
{
    public JsonNode toJson();
}
