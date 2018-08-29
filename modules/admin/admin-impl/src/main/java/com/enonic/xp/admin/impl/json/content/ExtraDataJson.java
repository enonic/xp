package com.enonic.xp.admin.impl.json.content;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.schema.xdata.XDataName;

public class ExtraDataJson
{
    private String name;

    private List<PropertyArrayJson> data;

    public ExtraDataJson( final ExtraData extraData )
    {
        this.name = extraData.getName().toString();
        this.data = PropertyTreeJson.toJson( extraData.getData() );
    }

    @JsonCreator
    public ExtraDataJson( @JsonProperty("name") final String name, @JsonProperty("data") final List<PropertyArrayJson> dataJsonList )
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
    public ExtraData getExtraData()
    {
        return new ExtraData( XDataName.from( name ), PropertyTreeJson.fromJson( data ) );
    }
}
