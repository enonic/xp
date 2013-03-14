package com.enonic.wem.core.content.schema.content.serializer;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.schema.content.form.inputtype.AbstractInputTypeConfigJsonSerializer;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypeConfig;
import com.enonic.wem.core.support.serializer.JsonParsingException;

public class InputTypeConfigJsonSerializer
{
    public InputTypeConfig parse( final JsonNode inputTypeConfigNode, final Class inputTypeClass )
    {
        final String className = inputTypeClass.getPackage().getName() + "." + inputTypeClass.getSimpleName() + "Config";
        final String serializerClassName = className + "JsonSerializer";

        AbstractInputTypeConfigJsonSerializer parser = instantiateInputTypeConfigJsonParser( serializerClassName );
        return parser.parseConfig( inputTypeConfigNode );
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
            throw new JsonParsingException( "Failed to instantiate AbstractInputTypeConfigJsonSerializer", e );
        }
    }
}
