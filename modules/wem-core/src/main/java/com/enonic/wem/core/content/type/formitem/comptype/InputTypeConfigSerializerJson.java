package com.enonic.wem.core.content.type.formitem.comptype;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.formitem.comptype.AbstractInputTypeConfigSerializerJson;
import com.enonic.wem.api.content.type.formitem.comptype.InputTypeConfig;

public class InputTypeConfigSerializerJson
{
    public InputTypeConfig parse( final JsonNode inputTypeConfigNode )
    {
        final Iterator<Map.Entry<String, JsonNode>> it = inputTypeConfigNode.getFields();
        final Map.Entry<String, JsonNode> inputTypeConfigNodeEntry = it.next();
        final String className = inputTypeConfigNodeEntry.getKey();
        final String serializerClassName = className + "SerializerJson";

        AbstractInputTypeConfigSerializerJson parser = instantiateInputTypeConfigJsonParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigNodeEntry.getValue() );
    }

    private AbstractInputTypeConfigSerializerJson instantiateInputTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractInputTypeConfigSerializerJson) cls.newInstance();
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
