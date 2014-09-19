package com.enonic.wem.api.schema.metadata;

public interface MetadataSchemaService
{
    MetadataSchemas getAll();

    MetadataSchema getByName( GetMetadataSchemaParams params );

    MetadataSchemas getByNames( GetMetadataSchemasParams params );
}
