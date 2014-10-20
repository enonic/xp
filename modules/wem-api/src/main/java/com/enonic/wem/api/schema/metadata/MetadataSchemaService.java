package com.enonic.wem.api.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;

public interface MetadataSchemaService
{
    MetadataSchemas getAll();

    MetadataSchema getByName( GetMetadataSchemaParams params );

    MetadataSchemas getByNames( GetMetadataSchemasParams params );

    MetadataSchemas getByModule( ModuleKey moduleKey );
}
