package com.enonic.xp.lib.node.mapper;

import java.util.List;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.FieldValues;
import com.enonic.xp.node.NodeQuery;
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

    void serialize( final MapGenerator gen, final FieldValues fields )
    {
        if ( fields != null && !fields.isEmpty() )
        {
            gen.map( "fields" );
            for ( final String field : fields.getFields() )
            {
                // the index keys are lowercase; hits show the field under the name a node shows it by
                final String name = NodeQuery.SUPPORTED_RETURN_FIELDS.getOrDefault( IndexPath.from( field ), field );
                final List<Object> values = fields.getValues( field );
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
