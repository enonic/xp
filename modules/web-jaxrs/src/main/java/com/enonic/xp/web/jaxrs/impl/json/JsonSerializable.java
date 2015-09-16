package com.enonic.xp.web.jaxrs.impl.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonSerializable
{
    public JsonNode toJson();
}
