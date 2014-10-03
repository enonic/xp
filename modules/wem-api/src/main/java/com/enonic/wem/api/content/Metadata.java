package com.enonic.wem.api.content;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

public class Metadata
{

    private MetadataSchemaName name;

    private RootDataSet data;

    public Metadata( final MetadataSchemaName name, final RootDataSet data )
    {
        this.name = name;
        this.data = data;
    }

    public RootDataSet getData()
    {
        return data;
    }

    public void setData( final RootDataSet data )
    {
        this.data = data;
    }

    public MetadataSchemaName getName()
    {
        return name;
    }

    public void setName( final MetadataSchemaName name )
    {
        this.name = name;
    }

}
