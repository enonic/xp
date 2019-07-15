package com.enonic.xp.admin.impl.rest.resource.layer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.layer.ContentLayerName;

public class DeleteLayerJson
{
    private String name;

    @JsonCreator
    public DeleteLayerJson( @JsonProperty(value = "name", required = true) final String name )
    {
        this.name = name;
    }

    @JsonIgnore
    public ContentLayerName getContentLayerName()
    {
        return ContentLayerName.from( name );
    }
}
