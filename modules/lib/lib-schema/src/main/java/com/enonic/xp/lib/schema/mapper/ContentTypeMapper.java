package com.enonic.xp.lib.schema.mapper;

import java.util.List;

import com.enonic.xp.resource.DynamicContentSchemaType;
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

        gen.value( "superType", descriptor.getSuperType() );
        gen.value( "abstract", descriptor.isAbstract() );
        gen.value( "final", descriptor.isFinal() );
        gen.value( "allowChildContent", descriptor.allowChildContent() );

        gen.array( "allowChildContentType" );
        final List<String> allowChildContentType = descriptor.getAllowChildContentType();
        if ( allowChildContentType != null )
        {
            for ( final String name : allowChildContentType )
            {
                gen.value( name );
            }
        }
        gen.end();

        gen.value( "displayNamePlaceholder", descriptor.getDisplayNamePlaceholder() );
        gen.value( "displayNamePlaceholderI18nKey", descriptor.getDisplayNamePlaceholderI18nKey() );
        gen.value( "displayNameExpression", descriptor.getDisplayNameExpression() );
        gen.value( "displayNameListExpression", descriptor.getDisplayNameListExpression() );

        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
        gen.value( "config", descriptor.getSchemaConfig().toRawJs() );
    }

    @Override
    protected String getType()
    {
        return DynamicContentSchemaType.CONTENT_TYPE.name();
    }
}
