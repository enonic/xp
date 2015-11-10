package com.enonic.xp.admin.impl.rest.resource.repo;

import com.fasterxml.jackson.databind.JsonNode;

public final class UpdateIndexSettingsRequestJson
{
    public String indexName;

    public JsonNode settings;
}
