package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.content.Metadata;
import com.enonic.xp.core.data.PropertyArrayJson;
import com.enonic.xp.core.data.PropertyTreeJson;
import com.enonic.xp.core.schema.mixin.MixinName;

public class MetadataJson
{
    private String name;

    private List<PropertyArrayJson> data;

    public MetadataJson( final Metadata metadata )
    {
        this.name = metadata.getName().toString();
        this.data = PropertyTreeJson.toJson( metadata.getData() );
    }

    @JsonCreator
    public MetadataJson( @JsonProperty("name") final String name, @JsonProperty("data") final List<PropertyArrayJson> dataJsonList )
    {
        this.name = name;
        this.data = dataJsonList;
    }

    public String getName()
    {
        return name;
    }

    public List<PropertyArrayJson> getData()
    {
        return data;
    }

    @JsonIgnore
    public Metadata getMetadata()
    {
        return new Metadata( MixinName.from( name ), PropertyTreeJson.fromJson( data ) );
    }
}
