package com.enonic.wem.admin.json.content;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.DataSetJson;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

public class MetadataJson
{

    private String name;

    private DataSetJson data;

    public MetadataJson( final Metadata metadata )
    {
        this.name = metadata.getName().toString();
        this.data = new DataSetJson( metadata.getData() );
    }

    @JsonCreator
    public MetadataJson( @JsonProperty("name") final String name, @JsonProperty("data") final List<DataJson> dataJsonList )
    {
        this.name = name;
        final List<Data> dataList = new ArrayList<>( dataJsonList.size() );
        for ( DataJson dataJson : dataJsonList )
        {
            dataList.add( dataJson.getData() );
        }
        this.data = new DataSetJson( DataSet.create().data( dataList ).build() );
    }

    public String getName()
    {
        return name;
    }

    public List<DataJson> getData()
    {
        return data.getValue();
    }

    @JsonIgnore
    public Metadata getMetadata()
    {
        return new Metadata( MetadataSchemaName.from( this.name ), this.data.getData().toRootDataSet() );
    }
}
