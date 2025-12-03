package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSchemaRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaRegistry.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConcurrentMap<String, JsonSchemaDefinitionWrapper> schemasMap = new ConcurrentHashMap<>();

    public void activate( final BundleContext bundleContext )
    {
        registerBuiltinSchemas( bundleContext );
    }

    public String register( final String jsonSchemaDef )
    {
        try
        {
            final JsonNode schemaNode = MAPPER.readTree( jsonSchemaDef );
            final String schemaId = Objects.requireNonNull( schemaNode.get( "$id" ), "$id must be set" ).asText();
            schemasMap.put( schemaId, new JsonSchemaDefinitionWrapper( jsonSchemaDef ) );
            return schemaId;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public String getJsonSchema( final String schemaId )
    {
        return Optional.ofNullable( schemasMap.get( schemaId ) )
            .map( JsonSchemaDefinitionWrapper::schema )
            .orElseThrow( () -> new IllegalStateException( "No schema found for " + schemaId ) );
    }

    public Map<String, String> getAllSchemas()
    {
        return schemasMap.entrySet()
            .stream()
            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, entry -> entry.getValue().schema() ) );
    }

    private void registerBuiltinSchemas( final BundleContext bundleContext )
    {
        schemasMap.clear();

        final Enumeration<URL> predefinedSchemas = bundleContext.getBundle().findEntries( "/META-INF/schemas", "*.json", true );

        if ( predefinedSchemas == null )
        {
            return;
        }

        while ( predefinedSchemas.hasMoreElements() )
        {
            final URL schemaURL = predefinedSchemas.nextElement();

            try (InputStream inputStream = schemaURL.openStream())
            {
                final String schemaDefinition = new String( inputStream.readAllBytes(), StandardCharsets.UTF_8 );
                final String schemaId = register( schemaDefinition );
                LOG.debug( "JSON Schema Definition: {} is registered", schemaId );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }
    }
}
