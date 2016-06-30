package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.databind.JsonNode;

public final class UpdateIndexSettingsRequestJson
{
    public String indexName;

    public JsonNode settings;
}
