package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

public class Metadata
{
    private MetadataSchemaName name;

    private PropertyTree data;

    public Metadata( final MetadataSchemaName name, final PropertyTree data )
    {
        Preconditions.checkNotNull( data, "data cannot be null" );
        this.name = name;
        this.data = data;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public void setData( final PropertyTree data )
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
