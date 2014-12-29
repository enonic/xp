package com.enonic.wem.core.schema.metadata;

import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.metadata.MetadataSchemaProvider;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public final class BundleMetadataSchemaProvider
    implements MetadataSchemaProvider
{
    private final MetadataSchemas schemas;

    private BundleMetadataSchemaProvider( final MetadataSchemas schemas )
    {
        this.schemas = schemas;
    }

    @Override
    public MetadataSchemas get()
    {
        return this.schemas;
    }

    public static BundleMetadataSchemaProvider create( final Bundle bundle )
    {
        final MetadataSchemas schemas = new MetadataSchemaLoader( bundle ).load();
        return schemas != null ? new BundleMetadataSchemaProvider( schemas ) : null;
    }
}
