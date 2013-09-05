package com.enonic.wem.admin.json.data;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;

public class DataSetJson
    extends DataJson
{
    private final ImmutableList<DataJson> list;

    private final DataSet dataSet;

    public DataSetJson( final DataSet dataSet )
    {
        this.dataSet = dataSet;

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

    public String getName()
    {
        return dataSet.getName();
    }

    public String getPath()
    {
        return dataSet.getPath().toString();
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
