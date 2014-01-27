package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.entity.PropertyIndexConfig;

public class PropertyIndexConfigJson
{
    private final Boolean enabled;

    private final Boolean fulltextEnabled;

    private final Boolean nGramEnabled;

    public PropertyIndexConfigJson( final PropertyIndexConfig propertyIndexConfig )
    {
        this.enabled = propertyIndexConfig.enabled();
        this.fulltextEnabled = propertyIndexConfig.fulltextEnabled();
        this.nGramEnabled = propertyIndexConfig.tokenizeEnabled();
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public PropertyIndexConfigJson( @JsonProperty("enabled") final boolean enabled,
                                    @JsonProperty("fulltextEnabled") final boolean fulltextEnabled,
                                    @JsonProperty("nGramEnabled") final boolean nGramEnabled )
    {
        this.enabled = enabled;
        this.fulltextEnabled = fulltextEnabled;
        this.nGramEnabled = nGramEnabled;
    }

    @JsonIgnore
    public PropertyIndexConfig toPropertyIndexConfig()
    {
        return PropertyIndexConfig.
            newPropertyIndexConfig().
            enabled( this.enabled ).
            fulltextEnabled( this.fulltextEnabled ).
            nGramEnabled( this.nGramEnabled ).
            build();
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
    public Boolean getnGramEnabled()
    {
        return nGramEnabled;
    }
}
