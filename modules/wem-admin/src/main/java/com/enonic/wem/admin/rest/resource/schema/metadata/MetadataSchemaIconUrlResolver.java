package com.enonic.wem.admin.rest.resource.schema.metadata;

import com.enonic.wem.admin.rest.resource.schema.IconUrlResolver;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

public final class MetadataSchemaIconUrlResolver
    extends IconUrlResolver
{

    public static final String REST_SCHEMA_ICON_URL = "/admin/rest/schema/metadata/icon/";

    private final MetadataSchemaIconResolver metadataSchemaIconResolver;

    public MetadataSchemaIconUrlResolver( final MetadataSchemaIconResolver metadataSchemaIconResolver )
    {
        this.metadataSchemaIconResolver = metadataSchemaIconResolver;
    }

    public String resolve( final MetadataSchema metadataSchema )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + metadataSchema.getName().toString();
        final Icon icon = metadataSchema.getIcon();
        return generateIconUrl( baseUrl, icon );
    }

    public String resolve( final MetadataSchemaName metadataSchemaName )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + metadataSchemaName.toString();
        final Icon icon = metadataSchemaIconResolver.resolveIcon( metadataSchemaName );
        return generateIconUrl( baseUrl, icon );
    }
}
