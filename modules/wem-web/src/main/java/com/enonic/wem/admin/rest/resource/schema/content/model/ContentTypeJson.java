package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentType;

public class ContentTypeJson
    extends AbstractContentTypeJson
{
    private final ContentTypeResultJson model;
    private final String iconUrl;

    public ContentTypeJson( final ContentType contentType )
    {
        this.model = new ContentTypeResultJson( contentType );
        this.iconUrl = SchemaImageUriResolver.resolve( contentType.getSchemaKey() );
    }

    public ContentTypeResultJson getContentType()
    {
        return this.model;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
