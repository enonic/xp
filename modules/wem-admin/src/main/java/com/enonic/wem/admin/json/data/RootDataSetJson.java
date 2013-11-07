package com.enonic.wem.admin.json.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;

// TODO: Do we need this class (currently not in use)
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

        this.list = null;
        this.rootDataSet = null;
    }


    public List<DataJson> getValue()
    {
        return this.list;
    }
}
