package com.enonic.xp.core.impl.schema;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

@Component(immediate = true)
public class JsonSchemaServiceImpl
    implements JsonSchemaService
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );

    private final JsonSchemaRegistry jsonSchemaRegistry;

    private JsonSchemaFactory schemaFactory;

    public JsonSchemaServiceImpl( final JsonSchemaRegistry jsonSchemaRegistry )
    {
        this.jsonSchemaRegistry = jsonSchemaRegistry;
    }

    public void activate()
    {
        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );

        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        jsonSchemaRegistry.register( formItemsSchema );

        this.schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012, builder -> builder.schemaLoaders(
            loader -> loader.schemas( jsonSchemaRegistry.getAllSchemas() ) ) );
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
            final StringBuilder message = new StringBuilder( "Validation errors:" );
            errors.forEach( err -> message.append( "\n" ).append( "- " ).append( err.getMessage() ) );
            LOG.info( message.toString() );
        }

        return valid;
    }
}
