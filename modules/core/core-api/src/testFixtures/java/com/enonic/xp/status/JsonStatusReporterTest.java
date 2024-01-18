package com.enonic.xp.status;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.json.ObjectMapperHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class JsonStatusReporterTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectReader OBJECT_READER = MAPPER.reader();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
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

    protected void assertJson( final String fileName, final JsonNode actualNode )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        assertEquals( expectedStr, actualStr );
    }
}
