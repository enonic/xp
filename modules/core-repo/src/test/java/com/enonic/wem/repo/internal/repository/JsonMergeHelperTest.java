package com.enonic.wem.repo.internal.repository;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.Assert.*;

public class JsonMergeHelperTest
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
        throws Exception
    {
        this.mapper = new ObjectMapper();
        mapper.enable( SerializationFeature.INDENT_OUTPUT );
    }

    @Test
    public void merge()
        throws Exception
    {
        final JsonNode defaultSettings = mapper.readTree( this.getClass().getResourceAsStream( "default.json" ) );
        final JsonNode overrideSettings = mapper.readTree( this.getClass().getResourceAsStream( "override.json" ) );

        final JsonNode merge = JsonMergeHelper.merge( defaultSettings, overrideSettings );

        assertJson( merge );
    }

    private void assertJson( final JsonNode node )
        throws IOException
    {
        final String nodeAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( node );

        final JsonNode expectedNode = mapper.readTree( this.getClass().getResourceAsStream( "merge_result.json" ) );

        final String expectedAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( expectedNode );

        assertEquals( expectedAsString, nodeAsString );
    }
}