package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.xdata.XData;

public final class XDataMapper
    extends SchemaMapper
{
    public XDataMapper( final DynamicSchemaResult<XData> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "XDATA";
    }
}
