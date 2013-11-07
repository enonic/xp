package com.enonic.wem.admin.json.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;

public class DataSetJson
    extends DataJson<DataSet>
{
    private final ImmutableList<DataJson> list;

    public DataSetJson( final DataSet dataSet )
    {
        super( dataSet );

        final ImmutableList.Builder<DataJson> builder = ImmutableList.builder();
        for ( final Data data : dataSet )
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
    public DataSetJson( @JsonProperty("name") final String name, @JsonProperty("type") final String type,
                        @JsonProperty("value") final List<DataJson> datas )
    {
        super( DataSet.newDataSet().name( name ).data( unwrapData( datas ) ).build() );

        final ImmutableList.Builder<DataJson> builder = ImmutableList.builder();
        for ( final Data data : getData() )
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

    private static Iterable<Data> unwrapData( final List<DataJson> dataJsonList )
    {
        final List<Data> dataList = new ArrayList<>( dataJsonList.size() );
        for ( DataJson dataJson : dataJsonList )
        {
            dataList.add( dataJson.getData() );
        }
        return dataList;
    }

    public String getType()
    {
        return DataSet.class.getSimpleName();
    }

    public List<DataJson> getValue()
    {
        return this.list;
    }
}
