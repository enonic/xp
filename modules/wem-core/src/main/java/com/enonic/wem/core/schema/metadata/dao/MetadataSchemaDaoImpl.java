package com.enonic.wem.core.schema.metadata.dao;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataRegistry;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public final class MetadataSchemaDaoImpl
    implements MetadataSchemaDao
{
    private MetadataRegistry metadataRegistry;

    @Override
    public MetadataSchemas getAllMetadataSchemas()
    {
        return this.metadataRegistry.getAllMetadataSchemas();
    }

    @Override
    public MetadataSchema getMetadataSchema( final MetadataSchemaName metadataSchemaName )
    {
        return this.metadataRegistry.getMetadata( metadataSchemaName );
    }

    @Override
    public MetadataSchemas getByModule( final ModuleKey moduleKey )
    {
        return this.metadataRegistry.getMetadataByModule( moduleKey );
    }

    public void setMetadataRegistry( final MetadataRegistry metadataRegistry )
    {
        this.metadataRegistry = metadataRegistry;
    }
}
