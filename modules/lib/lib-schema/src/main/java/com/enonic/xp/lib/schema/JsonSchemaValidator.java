package com.enonic.xp.lib.schema;

import java.util.Set;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class JsonSchemaValidator
{
    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012 );

    private static final JsonSchema SCHEMA = loadSchema();

    private static JsonSchema loadSchema()
    {
        final JsonSchema schema = FACTORY.getSchema( SchemaLocation.of( "classpath:schemas/content-type.schema.json" ) );
        schema.initializeValidators();
        return schema;
    }

    public static Set<ValidationMessage> validate( String yamlAsString )
    {
        return SCHEMA.validate( yamlAsString, InputFormat.YAML, ctx -> ctx.getExecutionConfig().setFormatAssertionsEnabled( true ) );
    }
}
