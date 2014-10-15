package com.enonic.wem.core.schema.content.dao;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeRegistry;
import com.enonic.wem.api.schema.content.ContentTypes;

public final class ContentTypeDaoImpl
    implements ContentTypeDao
{
    private ContentTypeRegistry contentTypeRegistry;

    @Override
    public ContentTypes getAllContentTypes()
    {
        return this.contentTypeRegistry.getAllContentTypes();
    }

    @Override
    public ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return this.contentTypeRegistry.getContentType( contentTypeName );
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        return this.contentTypeRegistry.getContentTypesByModule( moduleKey );
    }

    public void setContentTypeRegistry( final ContentTypeRegistry contentTypeRegistry )
    {
        this.contentTypeRegistry = contentTypeRegistry;
    }
}
