package com.enonic.wem.admin.rest.resource.schema.metadata;


import com.enonic.wem.api.Icon;
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

    public Icon resolveIcon( final MetadataSchemaName name )
    {
        final MetadataSchema metadataSchema = metadataSchemaService.getByName( name );
        return metadataSchema == null ? null : metadataSchema.getIcon();
    }

}
