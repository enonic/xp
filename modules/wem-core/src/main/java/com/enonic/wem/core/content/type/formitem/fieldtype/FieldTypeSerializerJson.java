package com.enonic.wem.core.content.type.formitem.fieldtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;

public class FieldTypeSerializerJson
{
    public void generate( final FieldType fieldType, final JsonGenerator g )
        throws IOException
    {
        BaseFieldType baseFieldType = (BaseFieldType) fieldType;

        g.writeFieldName( "fieldType" );
        g.writeStartObject();
        g.writeStringField( "className", baseFieldType.getClassName() );
        g.writeEndObject();
    }

    public FieldType parse( final JsonNode node )
    {
        String className = JsonParserUtil.getStringValue( "className", node );

        return instantiate( className );
    }

    private static FieldType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (FieldType) clazz.newInstance();
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
}
