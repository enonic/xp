package com.enonic.xp.lib.node.mapper;

import java.util.List;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.index.FieldValues;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

abstract class AbstractQueryResultMapper
    implements MapSerializable
{

    void serialize( final MapGenerator gen, final Aggregations aggregations )
    {
        if ( aggregations != null )
        {
            gen.map( "aggregations" );
            new AggregationMapper( aggregations ).serialize( gen );
            gen.end();
        }
    }

    /**
     * Writes the requested fields under the names by which they were requested. The values are keyed by index path and are therefore
     * lowercase, so the request is traversed rather than the result in order that a hit reads back as it was requested.
     */
    void serialize( final MapGenerator gen, final FieldValues fields, final List<String> returns )
    {
        if ( fields == null || fields.isEmpty() || returns == null || returns.isEmpty() )
        {
            return;
        }

        gen.map( "fields" );
        for ( final String name : returns )
        {
            final List<Object> values = fields.getValues( name );
            if ( values.isEmpty() )
            {
                continue;
            }
            if ( values.size() == 1 )
            {
                // single values come back as scalars, like property values do everywhere else in the JS API
                gen.value( name, values.get( 0 ) );
            }
            else
            {
                gen.array( name );
                for ( final Object value : values )
                {
                    gen.value( value );
                }
                gen.end();
            }
        }
        gen.end();
    }

    void serialize( final MapGenerator gen, final HighlightedProperties highlightedProperties )
    {
        if ( highlightedProperties != null && !highlightedProperties.isEmpty() )
        {
            gen.map( "highlight" );
            for ( HighlightedProperty highlightedProperty : highlightedProperties )
            {
                gen.array( highlightedProperty.getName() );
                for ( String fragment : highlightedProperty.getFragments() )
                {
                    gen.value( fragment );
                }
                gen.end();
            }
            gen.end();
        }
    }

    void serialize( final MapGenerator gen, final QueryExplanation explanation )
    {
        if ( explanation != null )
        {
            gen.map( "explanation" );
            doAddExplanation( gen, explanation );
            gen.end();
        }
    }

    private void doAddExplanation( final MapGenerator gen, final QueryExplanation explanation )
    {
        gen.value( "value", explanation.getValue() );
        gen.value( "description", explanation.getDescription() );
        gen.array( "details" );
        for ( final QueryExplanation detail : explanation.getDetails() )
        {
            gen.map();
            doAddExplanation( gen, detail );
            gen.end();
        }
        gen.end();
    }


}
