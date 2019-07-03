package com.enonic.xp.admin.impl.rest.resource.layer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.layer.CreateContentLayerParams;

public class CreateLayerJson
{
    private String name;

    private String parentName;

    private String displayName;

    @JsonCreator
    public CreateLayerJson( @JsonProperty(value = "name", required = true) final String name,
                            @JsonProperty("parentName") final String parentName,
                            @JsonProperty(value = "displayName", required = true) final String displayName )
    {
        this.name = name;
        this.parentName = parentName;
        this.displayName = displayName;
    }

    @JsonIgnore
    public CreateContentLayerParams getParams()
    {
        final ContentLayerName contentLayerName = ContentLayerName.from( name );
        final ContentLayerName parentContentLayerName = parentName == null ? null : ContentLayerName.from( name );
        return CreateContentLayerParams.create().
            name( contentLayerName ).
            parentName( parentContentLayerName ).
            displayName( displayName ).
            build();
    }
}
