package com.enonic.xp.impl.server.rest.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApplicationParams
{
    @JsonProperty("key")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> key;

    public Set<String> getKey()
    {
        return key;
    }

    public void setKey( Set<String> key )
    {
        this.key = key;
    }

}
