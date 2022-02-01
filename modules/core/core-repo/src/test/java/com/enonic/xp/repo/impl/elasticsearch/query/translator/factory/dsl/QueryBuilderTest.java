package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.core.impl.PropertyTreeMarshallerServiceFactory;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.util.JsonHelper;

public abstract class QueryBuilderTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    private static final PropertyTreeMarshallerService MARSHALLER_SERVICE =
        PropertyTreeMarshallerServiceFactory.newInstance( Mockito.mock( MixinService.class ) );

    protected PropertyTree readJson( final String value )
    {
        final JsonNode jsonNode = JsonHelper.from( value );

        return MARSHALLER_SERVICE.marshal( MAPPER.convertValue( jsonNode, new TypeReference<Map<String, Object>>()
        {

        } ) );
    }

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
