package com.enonic.wem.admin.json.schema.metadata;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.SchemaJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.metadata.MetadataSchema;

public class MetadataSchemaJson
    extends SchemaJson
    implements ItemJson
{
    private final MetadataSchema metadataSchema;

    private final boolean editable;

    private final boolean deletable;

    public MetadataSchemaJson( final MetadataSchema metadataSchema, final SchemaIconUrlResolver iconUrlResolver )
    {
        super( metadataSchema, iconUrlResolver );
        this.metadataSchema = metadataSchema;
        this.editable = true;
        this.deletable = true;
    }

    public String getCreator()
    {
        return metadataSchema.getCreator() != null ? metadataSchema.getCreator().toString() : null;
    }

    public String getModifier()
    {
        return metadataSchema.getModifier() != null ? metadataSchema.getModifier().toString() : null;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
