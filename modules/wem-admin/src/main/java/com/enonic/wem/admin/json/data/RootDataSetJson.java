package com.enonic.wem.admin.json.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;

public class RootDataSetJson
{
    private final ImmutableList<DataJson> list;

    private final RootDataSet rootDataSet;

    public RootDataSetJson( final RootDataSet rootDataSet )
    {
        this.rootDataSet = rootDataSet;

        final ImmutableList.Builder<DataJson> builder = ImmutableList.builder();
        for ( final Data data : rootDataSet )
        {
            if ( data instanceof Property )
            {
                builder.add( new PropertyJson( (Property) data ) );
            }
            else
            {
                builder.add( new DataSetJson( (DataSet) data ) );
            }
        }

        this.list = builder.build();
    }

    @JsonCreator
    public RootDataSetJson( @JsonProperty("value") final List<DataJson> datas )
    {
        ImmutableList.Builder<DataJson> listBuilder = ImmutableList.builder();
        this.rootDataSet = new RootDataSet();
        for ( DataJson dataJson : datas )
        {
            this.rootDataSet.add( dataJson.getData() );
            listBuilder.add( dataJson );
        }
        this.list = listBuilder.build();
    }

    public List<DataJson> getValue()
    {
        return this.list;
    }

    @JsonIgnore
    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }
}
