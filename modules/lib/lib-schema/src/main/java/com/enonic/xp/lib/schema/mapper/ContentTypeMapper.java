package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.script.serializer.MapGenerator;

public final class ContentTypeMapper
    extends SchemaMapper<ContentType>
{
    public ContentTypeMapper( final DynamicSchemaResult<ContentType> schema )
    {
        super( schema );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
        DynamicSchemaSerializer.serializeConfig( gen, descriptor.getSchemaConfig() );
    }

    @Override
    protected String getType()
    {
        return "CONTENT_TYPE";
    }
}
