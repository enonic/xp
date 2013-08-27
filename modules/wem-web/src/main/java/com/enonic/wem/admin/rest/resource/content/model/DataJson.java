package com.enonic.wem.admin.rest.resource.content.model;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;

public class DataJson
{
    private final AbstractDataJson dataJson;

    public DataJson( final Data data )
    {
        if ( data instanceof Property )
        {
            final Property property = (Property) data;
            dataJson = new PropertyJson( property );
        }
        else if ( data instanceof DataSet )
        {
            final DataSet dataSet = (DataSet) data;
            dataJson = new DataSetJson( dataSet );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown type of Data: " + data.getClass().getSimpleName() );
        }
    }

    public AbstractDataJson getData()
    {
        return dataJson;
    }
}
