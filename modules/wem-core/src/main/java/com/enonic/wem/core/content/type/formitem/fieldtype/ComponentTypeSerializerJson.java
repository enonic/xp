package com.enonic.wem.core.content.type.formitem.fieldtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;

public class ComponentTypeSerializerJson
{
    public void generate( final ComponentType componentType, final JsonGenerator g )
        throws IOException
    {
        BaseComponentType baseComponentType = (BaseComponentType) componentType;

        g.writeFieldName( "componentType" );
        g.writeStartObject();
        g.writeStringField( "className", baseComponentType.getClassName() );
        g.writeEndObject();
    }

    public ComponentType parse( final JsonNode node )
    {
        String className = JsonParserUtil.getStringValue( "className", node );

        return instantiate( className );
    }

    private static ComponentType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (ComponentType) clazz.newInstance();
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
