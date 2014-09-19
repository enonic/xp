package com.enonic.wem.core.schema.metadata.dao;

import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataName;
import com.enonic.wem.api.schema.metadata.Metadatas;

public interface MetadataDao
{
    Metadatas getAllMetadatas();

    Metadata getMetadata( MetadataName metadataName );
}
