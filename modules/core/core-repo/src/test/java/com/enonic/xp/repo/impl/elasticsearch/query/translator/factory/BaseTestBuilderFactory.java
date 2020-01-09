package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.elasticsearch.client.impl.EsClient;

public abstract class BaseTestBuilderFactory
{
    private static final String LINE_BREAK = System.getProperty( "line.separator" );

    private static final TimeZone ORIG_DEFAULT = TimeZone.getDefault();

    @BeforeEach
    public final void setup()
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
    }

    @BeforeEach
    public final void cleanup()
    {
        TimeZone.setDefault( ORIG_DEFAULT );
    }

    protected final String cleanString( final String input )
    {
        String output = input.replace( LINE_BREAK, "" );
        output = output.replaceAll( "\\s+", "" );
        return output;
    }

    protected final String getJson( final AggregationBuilder facetBuilder )
        throws Exception
    {
        final XContentBuilder builder = EsClient.jsonBuilder();
        builder.startObject();
        facetBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return Strings.toString( builder );
    }

    protected final String load( final String name )
        throws Exception
    {
        try (final InputStream stream = getClass().getResourceAsStream( name ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Cannot load test-resource with name [" + name + "] in [" + getClass().getPackage() + "]" );
        }
    }
}
