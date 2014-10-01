package com.enonic.wem.admin.json.schema.metadata;

import java.time.Instant;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.metadata.MetadataSchemaIconUrlResolver;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.schema.metadata.MetadataSchema;

public class MetadataSchemaJson
    implements ItemJson
{
    private final MetadataSchema metadataSchema;

    private final String iconUrl;

    public MetadataSchemaJson( final MetadataSchema metadataSchema, final MetadataSchemaIconUrlResolver iconUrlResolver )
    {
        this.metadataSchema = metadataSchema;
        this.iconUrl = iconUrlResolver.resolve( metadataSchema );
    }

    public String getKey()
    {
        return metadataSchema.getSchemaKey() != null ? metadataSchema.getSchemaKey().toString() : null;
    }

    public String getName()
    {
        return metadataSchema.getName() != null ? metadataSchema.getName().toString() : null;
    }

    public String getDisplayName()
    {
        return metadataSchema.getDisplayName();
    }

    public String getDescription()
    {
        return metadataSchema.getDescription();
    }

    public Instant getCreatedTime()
    {
        return metadataSchema.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return metadataSchema.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public FormJson getForm()
    {
        return new FormJson( metadataSchema.getForm() );
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
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
