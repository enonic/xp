package com.enonic.wem.core.schema.metadata.dao;

import javax.inject.Inject;

import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.api.schema.SchemaRegistry;

public final class MetadataSchemaSchemaDaoImpl
    implements MetadataSchemaDao
{
    private SchemaRegistry schemaRegistry;

    @Override
    public MetadataSchemas getAllMetadataSchemas()
    {
        return this.schemaRegistry.getAllMetadataSchemas();
    }

    @Override
    public MetadataSchema getMetadataSchema( final MetadataSchemaName metadataSchemaName )
    {
        return this.schemaRegistry.getMetadata( metadataSchemaName );
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}
