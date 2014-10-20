package com.enonic.wem.admin.json.schema.metadata;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.schema.metadata.MetadataSchemaIconUrlResolver;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public class MetadataSchemaListJson
{
    private final MetadataSchemas metadataSchemas;

    private final MetadataSchemaIconUrlResolver iconUrlResolver;

    public MetadataSchemaListJson( final MetadataSchemas metadataSchemas, final MetadataSchemaIconUrlResolver iconUrlResolver )
    {
        this.metadataSchemas = metadataSchemas;
        this.iconUrlResolver = iconUrlResolver;
    }

    public List<MetadataSchemaJson> getMetadataSchemas()
    {
        final ImmutableList.Builder<MetadataSchemaJson> builder = ImmutableList.builder();
        for ( MetadataSchema metadataSchema : metadataSchemas )
        {
            builder.add( new MetadataSchemaJson( metadataSchema, iconUrlResolver ) );
        }
        return builder.build();
    }

}
