package com.enonic.wem.core.content.type.formitem.comptype;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.comptype.AbstractComponentTypeConfigSerializerJson;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypeConfig;

public class ComponentTypeConfigSerializerJson
{
    public ComponentTypeConfig parse( final JsonNode componentTypeConfigNode )
    {
        final Iterator<Map.Entry<String, JsonNode>> it = componentTypeConfigNode.getFields();
        final Map.Entry<String, JsonNode> componentTypeConfigNodeEntry = it.next();
        final String className = componentTypeConfigNodeEntry.getKey();
        final String serializerClassName = className + "SerializerJson";

        AbstractComponentTypeConfigSerializerJson parser = instantiateComponentTypeConfigJsonParser( serializerClassName );
        return parser.parseConfig( componentTypeConfigNodeEntry.getValue() );
    }

    private AbstractComponentTypeConfigSerializerJson instantiateComponentTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractComponentTypeConfigSerializerJson) cls.newInstance();
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
