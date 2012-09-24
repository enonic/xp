package com.enonic.wem.core.content.type.formitem.comptype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.comptype.BaseComponentType;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentType;
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

    public BaseComponentType parse( final JsonNode node )
    {
        String className = JsonParserUtil.getStringValue( "className", node );

        return instantiate( className );
    }

    private static BaseComponentType instantiate( final String className )
    {
        Class clazz;
        try
        {
            clazz = Class.forName( className );
            return (BaseComponentType) clazz.newInstance();
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
