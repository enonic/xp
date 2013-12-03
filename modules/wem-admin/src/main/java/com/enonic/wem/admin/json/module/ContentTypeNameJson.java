package com.enonic.wem.admin.json.module;

import com.enonic.wem.api.schema.content.ContentTypeName;

public class ContentTypeNameJson
{
    private final ContentTypeName contentTypeName;

    public ContentTypeNameJson( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
    }

    public String getContentTypeName()
    {
        return contentTypeName.getContentTypeName();
    }
}
