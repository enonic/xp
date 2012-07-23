package com.enonic.wem.core.content.config.field.type;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

public class FieldTypeConfigProxyJsonParser
{
    public static FieldTypeConfig parse( final JsonNode node )
        throws IOException
    {
        Iterator<Map.Entry<String, JsonNode>> it = node.getFields();
        Map.Entry<String, JsonNode> fieldTypeConfigNodeEntry = it.next();
        String className = fieldTypeConfigNodeEntry.getKey();
        String parserClassName = className + "JsonParser";
        FieldTypeConfigJsonParser parser = instantiateFieldTypeConfigJsonParser( parserClassName );
        return parser.parse( fieldTypeConfigNodeEntry.getValue() );
    }

    private static FieldTypeConfigJsonParser instantiateFieldTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (FieldTypeConfigJsonParser) cls.newInstance();
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
