package com.enonic.wem.core.schema.content.dao;

import javax.inject.Inject;

import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;

public final class ContentTypeDaoImpl
    implements ContentTypeDao
{
    private SchemaRegistry schemaRegistry;

    @Override
    public ContentTypes getAllContentTypes()
    {
        return this.schemaRegistry.getAllContentTypes();
    }

    @Override
    public ContentType.Builder getContentType( final ContentTypeName contentTypeName )
    {
        return ContentType.newContentType( this.schemaRegistry.getContentType( contentTypeName ) );
    }

    @Inject
    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}
