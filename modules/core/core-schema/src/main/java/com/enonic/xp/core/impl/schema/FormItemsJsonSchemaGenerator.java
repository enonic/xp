package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

final class FormItemsJsonSchemaGenerator
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final List<String> PREDEFINED_SCHEMA_NAMES =
        List.of( "field-set", "item-set", "form-fragment", "option-set", "textline", "time", "tag", "textarea", "radiobutton",
                 "mediaselector", "long", "instant", "imageselector", "htmlarea", "geopoint", "double", "datetime", "date",
                 "customselector", "contenttypefilter", "contentselector", "combobox", "checkbox", "attachmentuploader" );

    private final Set<String> inputTypeSchemaIds;

    FormItemsJsonSchemaGenerator( final Set<String> inputTypeSchemaIds )
    {
        this.inputTypeSchemaIds = inputTypeSchemaIds;
    }

    public String generate()
    {
        final String jsonSchemaBaseUrl = "https://json-schema.enonic.com/8.0.0/";

        final String schemaId = jsonSchemaBaseUrl + "form-items.schema.json";

        final ObjectNode schema = MAPPER.createObjectNode();

        schema.put( "$schema", "https://json-schema.org/draft/2020-12/schema" );
        schema.put( "$id", schemaId );

        final ArrayNode oneOf = MAPPER.createArrayNode();

        PREDEFINED_SCHEMA_NAMES.forEach( schemaName -> oneOf.add( ref( jsonSchemaBaseUrl + schemaName + ".schema.json" ) ) );

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
