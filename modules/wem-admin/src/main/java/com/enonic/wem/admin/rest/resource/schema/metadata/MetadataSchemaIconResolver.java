package com.enonic.wem.admin.rest.resource.schema.metadata;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.metadata.GetMetadataSchemaParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;

public final class MetadataSchemaIconResolver
{
    private final MetadataSchemaService metadataSchemaService;

    public MetadataSchemaIconResolver( final MetadataSchemaService metadataSchemaService )
    {
        this.metadataSchemaService = metadataSchemaService;
    }

    public Icon resolveIcon( final MetadataSchemaName metadataSchemaName )
    {
        final MetadataSchema metadataSchema = metadataSchemaService.getByName( new GetMetadataSchemaParams( metadataSchemaName ) );
        return metadataSchema == null ? null : metadataSchema.getIcon();
    }

}
