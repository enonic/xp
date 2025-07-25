package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
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
        serializeXDatas( gen, descriptor.getXData() );
    }

    private void serializeXDatas( final MapGenerator gen, final XDataNames xDataNames )
    {
        if ( xDataNames != null && !xDataNames.isEmpty() )
        {
            gen.array( "xDataNames" );

            for ( XDataName xDataName : xDataNames )
            {
                gen.value( xDataName );
            }

            gen.end();
        }
    }

    @Override
    protected String getType()
    {
        return "CONTENT_TYPE";
    }
}
