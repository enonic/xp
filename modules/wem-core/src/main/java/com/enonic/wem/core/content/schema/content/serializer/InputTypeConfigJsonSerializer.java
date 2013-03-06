package com.enonic.wem.core.content.schema.content.serializer;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.schema.content.form.inputtype.AbstractInputTypeConfigJsonSerializer;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypeConfig;

public class InputTypeConfigJsonSerializer
{
    public InputTypeConfig parse( final JsonNode inputTypeConfigNode )
    {
        final Iterator<Map.Entry<String, JsonNode>> it = inputTypeConfigNode.getFields();
        final Map.Entry<String, JsonNode> inputTypeConfigNodeEntry = it.next();
        final String className = inputTypeConfigNodeEntry.getKey();
        final String serializerClassName = className + "JsonSerializer";

        AbstractInputTypeConfigJsonSerializer parser = instantiateInputTypeConfigJsonParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigNodeEntry.getValue() );
    }

    private AbstractInputTypeConfigJsonSerializer instantiateInputTypeConfigJsonParser( String className )
    {
        try
        {
            Class cls = Class.forName( className );

            return (AbstractInputTypeConfigJsonSerializer) cls.newInstance();
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
}
