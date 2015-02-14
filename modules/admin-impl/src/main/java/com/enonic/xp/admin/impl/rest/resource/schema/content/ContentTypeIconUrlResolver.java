package com.enonic.xp.admin.impl.rest.resource.schema.content;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.core.icon.Icon;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.content.ContentTypeName;

public final class ContentTypeIconUrlResolver
    extends IconUrlResolver
{

    public static final String REST_SCHEMA_ICON_URL = "/admin/rest/schema/content/icon/";

    private final ContentTypeIconResolver contentTypeIconResolver;

    public ContentTypeIconUrlResolver( final ContentTypeIconResolver contentTypeIconResolver )
    {
        this.contentTypeIconResolver = contentTypeIconResolver;
    }

    public String resolve( final ContentType contentType )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + contentType.getName().toString();
        final Icon icon = contentTypeIconResolver.resolveIcon( contentType );
        return generateIconUrl( baseUrl, icon );
    }

    public String resolve( final ContentTypeName contentTypeName )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + contentTypeName.toString();
        final Icon icon = contentTypeIconResolver.resolveIcon( contentTypeName );
        return generateIconUrl( baseUrl, icon );
    }
}
