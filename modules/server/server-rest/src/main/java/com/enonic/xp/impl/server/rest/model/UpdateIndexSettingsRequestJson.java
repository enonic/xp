package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.databind.JsonNode;

public final class UpdateIndexSettingsRequestJson
{
    public String repositoryId;

    public JsonNode settings;

    public boolean requireClosedIndex;
}
