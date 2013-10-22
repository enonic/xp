package com.enonic.wem.core.schema.content.serializer;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.form.inputtype.AbstractInputTypeConfigJsonSerializer;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
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
