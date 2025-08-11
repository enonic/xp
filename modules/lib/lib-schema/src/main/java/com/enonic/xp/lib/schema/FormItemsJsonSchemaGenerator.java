package com.enonic.xp.lib.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class FormItemsJsonSchemaGenerator
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Set<String> inputTypeSchemaIds;

    FormItemsJsonSchemaGenerator( final Set<String> inputTypeSchemaIds )
    {
        this.inputTypeSchemaIds = inputTypeSchemaIds;
    }

    public String generate()
    {
        final String schemaId = "https://xp.enonic.com/schemas/json/form-items.schema.json";

        final ObjectNode schema = MAPPER.createObjectNode();

        schema.put( "$schema", "https://json-schema.org/draft/2020-12/schema" );
        schema.put( "$id", schemaId );

        final ArrayNode oneOf = MAPPER.createArrayNode();

        oneOf.add( ref( "https://xp.enonic.com/schemas/json/field-set.schema.json" ) );
        oneOf.add( ref( "https://xp.enonic.com/schemas/json/item-set.schema.json" ) );
        oneOf.add( ref( "https://xp.enonic.com/schemas/json/inline-mixin.schema.json" ) );
        oneOf.add( ref( "https://xp.enonic.com/schemas/json/option-set.schema.json" ) );

        inputTypeSchemaIds.forEach( id -> oneOf.add( ref( id ) ) );

        schema.set( "oneOf", oneOf );

        try
        {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( schema );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private ObjectNode ref( final String refPath )
    {
        final ObjectNode ref = MAPPER.createObjectNode();
        ref.put( "$ref", refPath );
        return ref;
    }
}
