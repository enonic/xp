package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonSchemaRegistry
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaRegistry.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConcurrentMap<String, JsonSchemaDefinitionWrapper> schemasMap = new ConcurrentHashMap<>();

    public JsonSchemaRegistry()
    {
    }

    public void activate()
    {
        registerBuiltinSchemas();
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

    private void registerBuiltinSchemas()
    {
        schemasMap.clear();
        registerBuiltinSchemasFromDir( Paths.get( "src/main/resources/META-INF/schemas" ) );
    }

    private void registerBuiltinSchemasFromDir( final Path dirPath )
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream( dirPath, "*.json" ))
        {
            for ( Path path : stream )
            {
                final String schemaId = register( Files.readString( path ) );
                LOG.debug( "JSON Schema Definition: {} is registered", schemaId );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
