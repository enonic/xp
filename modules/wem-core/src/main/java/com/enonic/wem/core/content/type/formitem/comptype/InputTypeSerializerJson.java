package com.enonic.wem.core.content.type.formitem.comptype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.comptype.BaseInputType;
import com.enonic.wem.api.content.type.formitem.comptype.InputType;
import com.enonic.wem.core.content.JsonParserUtil;

public class InputTypeSerializerJson
{
    public void generate( final InputType inputType, final JsonGenerator g )
        throws IOException
    {
        BaseInputType baseInputType = (BaseInputType) inputType;

        g.writeFieldName( "inputType" );
        g.writeStartObject();
        g.writeStringField( "className", baseInputType.getClassName() );
        g.writeEndObject();
    }

    public BaseInputType parse( final JsonNode node )
    {
        String className = JsonParserUtil.getStringValue( "className", node );

        return instantiate( className );
    }

    private static BaseInputType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (BaseInputType) clazz.newInstance();
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
