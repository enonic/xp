package com.enonic.wem.core.content.type.configitem.fieldtype;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

public class FieldTypeConfigProxySerializerJson
{
    public static FieldTypeConfig parse( final JsonNode node )
    {
        Iterator<Map.Entry<String, JsonNode>> it = node.getFields();
        Map.Entry<String, JsonNode> fieldTypeConfigNodeEntry = it.next();
        String className = fieldTypeConfigNodeEntry.getKey();
        String parserClassName = className + "SerializerJson";
        FieldTypeConfigSerializerJson parser = instantiateFieldTypeConfigJsonParser( parserClassName );
        return parser.parse( fieldTypeConfigNodeEntry.getValue() );
    }

    private static FieldTypeConfigSerializerJson instantiateFieldTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (FieldTypeConfigSerializerJson) cls.newInstance();
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
