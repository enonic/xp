package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.Dialects;

@Component(immediate = true)
public class JsonSchemaServiceImpl
    implements JsonSchemaService
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );

    private final ConcurrentMap<String, JsonSchemaDefinitionWrapper> schemasMap = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    private volatile SchemaRegistry schemaRegistry;

    @Override
    public boolean registerSchema( final String schema )
    {
        return register( schema );
    }

    @Override
    public boolean loadSchemas( final List<URL> schemaURLs )
    {
        if ( schemaURLs == null )
        {
            return false;
        }

        boolean shouldBeUpdated = false;

        for ( URL schemaURL : schemaURLs )
        {
            try (InputStream inputStream = schemaURL.openStream())
            {
                final String schemaDefinition = new String( inputStream.readAllBytes(), StandardCharsets.UTF_8 );
                if ( register( schemaDefinition ) )
                {
                    shouldBeUpdated = true;
                }
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        return shouldBeUpdated;
    }

    @Override
    public void validate( final String schemaId, final String yml )
    {
        final SchemaRegistry factory = this.schemaRegistry;

        if ( factory == null )
        {
            throw new IllegalArgumentException( "Schema factory is not initialized" );
        }

        final Schema schema = schemaRegistry.getSchema( SchemaLocation.of( schemaId ) );

        final List<Error> errors = schema.validate( yml, InputFormat.YAML );

        if ( !errors.isEmpty() )
        {
            final StringBuilder builder = new StringBuilder( "Validation errors for schema \"" );
            builder.append( schemaId );
            builder.append( "\":" );
            errors.forEach( err -> builder.append( "\n - " ).append( err.getMessage() ) );
            throw new IllegalArgumentException( builder.toString() );
        }
    }

    @Override
    public void refreshSchemaRegistry()
    {
        synchronized ( lock )
        {
            this.schemaRegistry = SchemaRegistry.withDialect( Dialects.getDraft202012(), builder -> builder.schemas( getAllSchemas() ) );
        }
    }

    private boolean register( final String schemaDefinition )
    {
        try
        {
            final JsonNode schemaNode = ObjectMapperProvider.MAPPER.readTree( schemaDefinition );
            final String schemaId = Objects.requireNonNull( schemaNode.get( "$id" ), "$id must be set" ).asText();

            final JsonSchemaDefinitionWrapper persistedSchema = schemasMap.get( schemaId );

            final boolean changed = persistedSchema == null || !persistedSchema.schema().equals( schemaDefinition );
            if ( changed )
            {
                schemasMap.put( schemaId, new JsonSchemaDefinitionWrapper( schemaDefinition ) );
                LOG.debug( "JSON Schema Definition: {} is registered", schemaId );
            }

            return changed;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Map<String, String> getAllSchemas()
    {
        return schemasMap.entrySet()
            .stream()
            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, entry -> entry.getValue().schema() ) );
    }
}
