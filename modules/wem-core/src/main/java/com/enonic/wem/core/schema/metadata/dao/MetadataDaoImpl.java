package com.enonic.wem.core.schema.metadata.dao;

import javax.inject.Inject;

import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataName;
import com.enonic.wem.api.schema.metadata.Metadatas;
import com.enonic.wem.api.schema.SchemaRegistry;

public final class MetadataDaoImpl
    implements MetadataDao
{
    private SchemaRegistry schemaRegistry;

    @Override
    public Metadatas getAllMetadatas()
    {
        return this.schemaRegistry.getAllMetadatas();
    }

    @Override
    public Metadata getMetadata( final MetadataName metadataName )
    {
        return this.schemaRegistry.getMetadata( metadataName );
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}
