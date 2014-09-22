package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;

public final class XmlMetadataSchemaMapper
{
    public static XmlMetadataSchema toXml( final XmlMetadataSchema object )
    {
        final XmlMetadataSchema result = new XmlMetadataSchema();
        result.setDisplayName( object.getDisplayName() );
        result.setForm( object.getForm() );
        return result;
    }

    public static void fromXml( final XmlMetadataSchema xml, final MetadataSchema.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.form( XmlFormMapper.fromXml( xml.getForm() ) );
    }
}
