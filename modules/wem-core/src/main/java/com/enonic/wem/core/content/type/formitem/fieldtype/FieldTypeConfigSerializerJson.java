package com.enonic.wem.core.content.type.formitem.fieldtype;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

public class FieldTypeConfigSerializerJson
{
    public FieldTypeConfig parse( final JsonNode fieldTypeConfigNode )
    {
        final Iterator<Map.Entry<String, JsonNode>> it = fieldTypeConfigNode.getFields();
        final Map.Entry<String, JsonNode> fieldTypeConfigNodeEntry = it.next();
        final String className = fieldTypeConfigNodeEntry.getKey();
        final String serializerClassName = className + "SerializerJson";

        AbstractFieldTypeConfigSerializerJson parser = instantiateFieldTypeConfigJsonParser( serializerClassName );
        return parser.parseConfig( fieldTypeConfigNodeEntry.getValue() );
    }

    private AbstractFieldTypeConfigSerializerJson instantiateFieldTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractFieldTypeConfigSerializerJson) cls.newInstance();
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
