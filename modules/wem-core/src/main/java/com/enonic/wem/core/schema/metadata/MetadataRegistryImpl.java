package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataProvider;
import com.enonic.wem.api.schema.metadata.MetadataRegistry;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.core.schema.BaseRegistry;

public final class MetadataRegistryImpl
    extends BaseRegistry<MetadataProvider, MetadataSchema, MetadataSchemas, MetadataSchemaName>
    implements MetadataRegistry
{
    public MetadataRegistryImpl()
    {
        super( MetadataProvider.class );
    }

    public MetadataSchema getMetadata( final MetadataSchemaName name )
    {
        return super.getItemByName( name );
    }

    public MetadataSchemas getMetadataByModule( final ModuleKey moduleKey )
    {
        return super.getItemsByModule( moduleKey );
    }

    public MetadataSchemas getAllMetadataSchemas()
    {
        return MetadataSchemas.from( super.getAllItems() );
    }

}
