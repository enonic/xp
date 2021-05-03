package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CleanUpAuditLogRequestJson
{
    private final String ageThreshold;

    @JsonCreator
    public CleanUpAuditLogRequestJson( @JsonProperty("ageThreshold") final String ageThreshold )
    {
        this.ageThreshold = ageThreshold;
    }

    public String getAgeThreshold()
    {
        return ageThreshold;
    }
}
