package com.enonic.wem.api.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;

public interface MetadataRegistry
{

    MetadataSchema getMetadata( MetadataSchemaName metadataSchemaName );

    MetadataSchemas getMetadataByModule( ModuleKey moduleKey );

    MetadataSchemas getAllMetadataSchemas();

}
