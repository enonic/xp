package com.enonic.wem.api.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;

public interface MetadataSchemaService
{
    MetadataSchemas getAll();

    MetadataSchema getByName( MetadataSchemaName name );

    MetadataSchemas getByModule( ModuleKey moduleKey );
}
