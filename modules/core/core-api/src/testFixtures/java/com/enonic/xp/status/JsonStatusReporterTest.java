package com.enonic.xp.status;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class JsonStatusReporterTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        disable( SerializationFeature.FAIL_ON_EMPTY_BEANS ).
        enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY ).
        setSerializationInclusion( JsonInclude.Include.ALWAYS );

    private static final ObjectReader OBJECT_READER = MAPPER.reader();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

    protected JsonNode parseJson( final String json )
        throws Exception
    {
        return OBJECT_READER.readTree( json );
    }

    protected String toJson( final Object value )
        throws Exception
    {
        return OBJECT_WRITER.writeValueAsString( value );
    }
}
