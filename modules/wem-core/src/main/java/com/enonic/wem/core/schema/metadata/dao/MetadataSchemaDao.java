package com.enonic.wem.core.schema.metadata.dao;

import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public interface MetadataSchemaDao
{
    MetadataSchemas getAllMetadataSchemas();

    MetadataSchema getMetadataSchema( MetadataSchemaName metadataSchemaName );
}
