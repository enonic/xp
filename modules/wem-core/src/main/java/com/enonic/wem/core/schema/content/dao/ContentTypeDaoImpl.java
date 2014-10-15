package com.enonic.wem.core.schema.content.dao;

import com.enonic.wem.api.module.ModuleKey;
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
    public ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return this.schemaRegistry.getContentType( contentTypeName );
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        ContentTypes allContentTypes = this.schemaRegistry.getAllContentTypes();
        ContentTypes.Builder contentTypesBuilder = ContentTypes.newContentTypes();
        for ( ContentType contentType : allContentTypes )
        {
            if ( contentType.getName().getModuleKey().equals( moduleKey ))
            {
                contentTypesBuilder.add( contentType );
            }
        }

        return contentTypesBuilder.build();
    }

    public void setSchemaRegistry( final SchemaRegistry schemaRegistry )
    {
        this.schemaRegistry = schemaRegistry;
    }
}
