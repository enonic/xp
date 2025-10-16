package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.script.serializer.MapGenerator;

public final class XDataMapper
    extends SchemaMapper<XData>
{
    public XDataMapper( final DynamicSchemaResult<XData> schema )
    {
        super( schema );
    }

    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
    }

    @Override
    protected String getType()
    {
        return DynamicContentSchemaType.XDATA.name();
    }
}
