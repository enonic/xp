package com.enonic.xp.repo.impl.elasticsearch.query.builder;

import java.util.TimeZone;

import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.facet.FacetBuilder;
import org.junit.Before;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public abstract class BaseTestBuilderFactory
{
    private static final String LINE_BREAK = System.getProperty( "line.separator" );

    private static final TimeZone ORIG_DEFAULT = TimeZone.getDefault();

    @Before
    public final void setup()
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
    }

    @Before
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

    protected final String getJson( final FacetBuilder facetBuilder )
        throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        facetBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return builder.string();
    }

    protected final String load( final String name )
        throws Exception
    {
        return Resources.toString( getClass().getResource( name ), Charsets.UTF_8 );
    }
}
