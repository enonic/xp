package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.entity.PropertyIndexConfig;

public class PropertyIndexConfigJson
{
    private final Boolean enabled;

    private final Boolean fulltextEnabled;

    private final Boolean tokenizedEnabled;

    public PropertyIndexConfigJson( final PropertyIndexConfig propertyIndexConfig )
    {
        this.enabled = propertyIndexConfig.enabled();
        this.fulltextEnabled = propertyIndexConfig.fulltextEnabled();
        this.tokenizedEnabled = propertyIndexConfig.tokenizeEnabled();
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public PropertyIndexConfigJson( @JsonProperty("enabled") final boolean enabled,
                                    @JsonProperty("fulltextEnabled") final boolean fulltextEnabled,
                                    @JsonProperty("tokenizedEnabled") final boolean tokenizedEnabled )
    {
        this.enabled = enabled;
        this.fulltextEnabled = fulltextEnabled;
        this.tokenizedEnabled = tokenizedEnabled;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean getEnabled()
    {
        return enabled;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean getFulltextEnabled()
    {
        return fulltextEnabled;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean getTokenizedEnabled()
    {
        return tokenizedEnabled;
    }
}
