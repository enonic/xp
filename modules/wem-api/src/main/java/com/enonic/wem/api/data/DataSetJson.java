package com.enonic.wem.api.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

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
    public DataSetJson( @JsonProperty("name") final String name, @JsonProperty("set") final List<DataJson> datas )
    {
        super( DataSet.create().name( name ).data( unwrapData( datas ) ).build() );

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

    @JsonIgnore
    public List<DataJson> getValue()
    {
        return getSet();
    }

    public List<DataJson> getSet()
    {
        return this.list;
    }
}
