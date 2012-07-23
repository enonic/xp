package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class BaseFieldTypeJsonGenerator
    extends FieldTypeJsonGenerator
{
    public static final BaseFieldTypeJsonGenerator DEFAULT = new BaseFieldTypeJsonGenerator();

    @Override
    public void generate( final FieldType fieldType, final JsonGenerator g )
        throws IOException
    {
        AbstractBaseFieldType baseFieldType = (AbstractBaseFieldType) fieldType;

        g.writeFieldName( "fieldType" );
        g.writeStartObject();
        g.writeStringField( "className", baseFieldType.getClassName() );
        g.writeEndObject();
    }
}
