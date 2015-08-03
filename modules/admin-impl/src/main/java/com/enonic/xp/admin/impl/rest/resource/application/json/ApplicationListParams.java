package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;

public final class ApplicationListParams
{
    @JsonProperty("key")
    protected Set<String> keys;

    public Set<ApplicationKey> getKeys()
    {
        return this.keys.stream().map( ApplicationKey::from ).collect( Collectors.toSet() );
    }
}
