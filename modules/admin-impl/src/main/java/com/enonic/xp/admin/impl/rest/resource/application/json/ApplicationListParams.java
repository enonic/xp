package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.xp.app.ApplicationKey;

public final class ApplicationListParams
{
    private final Set<ApplicationKey> applicationKeys;

    @JsonCreator
    public ApplicationListParams( @JsonProperty("key") String... keys )
    {
        this.applicationKeys = Sets.newHashSet();
        for ( final String key : keys )
        {
            this.applicationKeys.add( ApplicationKey.from( key ) );
        }
    }

    public Set<ApplicationKey> getApplicationKeys()
    {
        return this.applicationKeys;
    }
}
