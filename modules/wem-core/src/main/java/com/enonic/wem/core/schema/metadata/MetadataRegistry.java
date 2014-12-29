package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public interface MetadataRegistry
{

    MetadataSchema getMetadata( MetadataSchemaName metadataSchemaName );

    MetadataSchemas getMetadataByModule( ModuleKey moduleKey );

    MetadataSchemas getAllMetadataSchemas();

}
