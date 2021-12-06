package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.json.ObjectMapperHelper;

public abstract class QueryBuilderTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    protected final String load( final String name )
        throws Exception
    {
        try (InputStream stream = getClass().getResourceAsStream( name ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Cannot load test-resource with name [" + name + "] in [" + getClass().getPackage() + "]" );
        }
    }

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        assertStringJson( load( fileName ), actualJson );
    }

    protected final void assertStringJson( final String expectedJson, final String actualJson )
        throws Exception
    {
        final JsonNode expectedNode = MAPPER.readTree( expectedJson );
        final JsonNode actualNode = MAPPER.readTree( actualJson );

        final String expectedStr = OBJECT_WRITER.writeValueAsString( expectedNode );
        final String actualStr = OBJECT_WRITER.writeValueAsString( actualNode );

        Assertions.assertEquals( expectedStr, actualStr );
    }

}
