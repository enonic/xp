package com.enonic.xp.admin.impl.rest.resource.project.layer.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.layer.ContentLayerKey;

public final class DeleteLayerParamsJson
{
    private final ContentLayerKey key;

    @JsonCreator
    DeleteLayerParamsJson( @JsonProperty("key") final String key )
    {
        this.key = ContentLayerKey.from( key );
    }

    public ContentLayerKey getKey()
    {
        return key;
    }
}
