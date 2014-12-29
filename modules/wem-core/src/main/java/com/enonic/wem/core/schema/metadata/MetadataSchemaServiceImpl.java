package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public class MetadataSchemaServiceImpl
    implements MetadataSchemaService
{
    private MetadataRegistry registry;

    @Override
    public MetadataSchema getByName( final MetadataSchemaName name )
    {
        return this.registry.getMetadata( name );
    }

    @Override
    public MetadataSchemas getByModule( final ModuleKey moduleKey )
    {
        return this.registry.getMetadataByModule( moduleKey );
    }

    @Override
    public MetadataSchemas getAll()
    {
        return this.registry.getAllMetadataSchemas();
    }

    public void setRegistry( final MetadataRegistry registry )
    {
        this.registry = registry;
    }
}
