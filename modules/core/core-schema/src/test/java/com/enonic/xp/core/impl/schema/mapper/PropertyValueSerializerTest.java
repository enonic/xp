package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PropertyValueSerializerTest
{
    private static final ObjectMapper YAML = new ObjectMapper( new YAMLFactory() );

    @Test
    void round_trip()
        throws Exception
    {
        final JsonNode parsed = YAML.readTree( """
                                                   text: "value"
                                                   number: 42
                                                   big: 4294967296
                                                   decimal: 1.5
                                                   flag: true
                                                   list:
                                                     - "a"
                                                     - 2
                                                   nested:
                                                     inner: "x"
                                                   """ );
        final GenericValue value = PropertyValueDeserializer.fromYml( parsed );

        final JsonNode serialized = PropertyValueSerializer.toYml( value, JsonNodeFactory.instance );

        assertEquals( parsed, serialized );
        assertEquals( value, PropertyValueDeserializer.fromYml( serialized ) );
    }

    @Test
    void strings_stay_strings()
    {
        final GenericValue value = GenericValue.newObject().put( "looksLikeNumber", "123" ).put( "looksLikeBoolean", "true" ).build();

        final String yaml = new YmlParserBase().toYaml( PropertyValueSerializer.toYml( value, JsonNodeFactory.instance ) );

        assertFalse( yaml.startsWith( "---" ), yaml );
        assertEquals( value, PropertyValueDeserializer.fromYml( readTree( yaml ) ) );
    }

    private static JsonNode readTree( final String yaml )
    {
        try
        {
            return YAML.readTree( yaml );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}