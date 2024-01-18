package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.util.JsonHelper;

public abstract class QueryBuilderTest
{
    private final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    protected PropertyTree readJson( final String value )
    {
        return PropertyTree.fromMap( JsonHelper.toMap( value ) );
    }

    protected final String load( final String name )
    {
        try (InputStream stream = getClass().getResourceAsStream( name ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Cannot load test-resource with name [" + name + "] in [" + getClass().getPackage() + "]", e );
        }
    }

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        jsonTestHelper.assertJsonEquals( jsonTestHelper.stringToJson( load( fileName ) ), jsonTestHelper.stringToJson( actualJson ) );
    }
}
