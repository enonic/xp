package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonSchemaRegistry
{
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
        return register( jsonSchemaDef, false );
    }

    public String registerInputType( final String jsonSchemaDef )
    {
        return register( jsonSchemaDef, true );
    }

    private String register( final String jsonSchemaDef, final boolean inputType )
    {
        try
        {
            final JsonNode schemaNode = MAPPER.readTree( jsonSchemaDef );
            final String schemaId = Objects.requireNonNull( schemaNode.get( "$id" ), "$id must be set" ).asText();
            schemasMap.put( schemaId, new JsonSchemaDefinitionWrapper( jsonSchemaDef, inputType ) );
            return schemaId;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public Set<String> getInputTypeSchemaIds()
    {
        return schemasMap.entrySet()
            .stream()
            .filter( entry -> entry.getValue().inputType() )
            .map( Map.Entry::getKey )
            .collect( Collectors.toUnmodifiableSet() );
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
        registerBuiltinSchemasFromDir( Paths.get( "src/main/resources/schemas/inputTypes" ), true );
        registerBuiltinSchemasFromDir( Paths.get( "src/main/resources/schemas" ), false );
    }

    private void registerBuiltinSchemasFromDir( final Path dirPath, final boolean inputType )
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream( dirPath, "*.json" ))
        {
            for ( Path path : stream )
            {
                final String schemaDef = Files.readString( path );
                register( schemaDef, inputType );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
