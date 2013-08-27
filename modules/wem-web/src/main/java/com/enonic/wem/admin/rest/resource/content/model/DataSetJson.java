package com.enonic.wem.admin.rest.resource.content.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;

public class DataSetJson
    extends AbstractDataJson
{
    private final ImmutableList<DataJson> list;

    private final DataSet dataSet;

    public DataSetJson( final DataSet dataSet )
    {
        this.dataSet = dataSet;

        final ImmutableList.Builder<DataJson> builder = ImmutableList.builder();
        for ( final Data data : dataSet )
        {
            builder.add( new DataJson( data ) );
        }

        this.list = builder.build();
    }

    public List<DataJson> getData()
    {
        return list;
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
