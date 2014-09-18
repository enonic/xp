package com.enonic.wem.api.schema.metadata;

public interface MetadataService
{
    Metadatas getAll();

    Metadata getByName( GetMetadataParams params );

    Metadatas getByNames( GetMetadatasParams params );
}
