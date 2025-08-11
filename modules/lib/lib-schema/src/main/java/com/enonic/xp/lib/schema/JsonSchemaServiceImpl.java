package com.enonic.xp.lib.schema;

import java.util.Set;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

public class JsonSchemaServiceImpl
    implements JsonSchemaService
{
    private final JsonSchemaRegistry jsonSchemaRegistry;

    private JsonSchemaFactory schemaFactory;

    public JsonSchemaServiceImpl( final JsonSchemaRegistry jsonSchemaRegistry )
    {
        this.jsonSchemaRegistry = jsonSchemaRegistry;
    }

    public void activate()
    {
        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator =
            new FormItemsJsonSchemaGenerator( jsonSchemaRegistry.getInputTypeSchemaIds() );

        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        jsonSchemaRegistry.register( formItemsSchema );

        this.schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012, builder -> builder.schemaLoaders(
            loader -> loader.schemas( jsonSchemaRegistry.getAllSchemas() ) ) );
    }

    @Override
    public String registerInputTypeSchema( final String jsonSchemaDefinition )
    {
        return jsonSchemaRegistry.registerInputType( jsonSchemaDefinition );
    }

    @Override
    public boolean isContentTypeValid( final String contentTypeAsYml )
    {
        return isSchemaValid( "https://xp.enonic.com/schemas/json/content-type.schema.json", contentTypeAsYml );
    }

    @Override
    public boolean isSchemaValid( final String schemaId, final String yml )
    {
        final JsonSchema schema = schemaFactory.getSchema( SchemaLocation.of( schemaId ) );
        schema.initializeValidators();

        Set<ValidationMessage> errors =
            schema.validate( yml, InputFormat.YAML, ctx -> ctx.getExecutionConfig().setFormatAssertionsEnabled( true ) );

        final boolean valid = errors.isEmpty();

        if ( !valid )
        {
            System.out.println( "Validation errors:" );
            errors.forEach( err -> System.out.println( "- " + err.getMessage() ) );
        }

        return valid;
    }
}
