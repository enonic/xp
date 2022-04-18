package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.content.ContentType;

public final class ContentTypeMapper
    extends SchemaMapper
{
    public ContentTypeMapper( final DynamicSchemaResult<ContentType> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "Content_Type";
    }
}
