package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.Map;
import java.util.function.Function;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

public class DslQueryParser
{
    private static final Map<String, Function<PropertySet, QueryBuilder>> QUERY_BUILDERS =
        Map.ofEntries( Map.entry( BooleanQueryBuilder.NAME, set -> new BooleanQueryBuilder( set ).create() ),
                       Map.entry( TermQueryBuilder.NAME, set -> new TermQueryBuilder( set ).create() ),
                       Map.entry( MatchQueryBuilder.NAME, set -> new MatchQueryBuilder( set ).create() ),
                       Map.entry( InQueryBuilder.NAME, set -> new InQueryBuilder( set ).create() ),
                       Map.entry( LikeQueryBuilder.NAME, set -> new LikeQueryBuilder( set ).create() ),
                       Map.entry( FulltextQueryBuilder.NAME, set -> new FulltextQueryBuilder( set ).create() ),
                       Map.entry( NgramQueryBuilder.NAME, set -> new NgramQueryBuilder( set ).create() ),
                       Map.entry( StemmedQueryBuilder.NAME, set -> new StemmedQueryBuilder( set ).create() ),
                       Map.entry( RangeQueryBuilder.NAME, set -> new RangeQueryBuilder( set ).create() ),
                       Map.entry( PathMatchQueryBuilder.NAME, set -> new PathMatchQueryBuilder( set ).create() ),
                       Map.entry( MatchAllQueryBuilder.NAME, set -> new MatchAllQueryBuilder( set ).create() ),
                       Map.entry( ExistsQueryBuilder.NAME, set -> new ExistsQueryBuilder( set ).create() ) );

    private DslQueryParser()
    {
    }

    public static QueryBuilder parseQuery( final Property property )
    {
        final Function<PropertySet, QueryBuilder> builder = QUERY_BUILDERS.get( property.getName() );
        if ( builder == null )
        {
            throw new IllegalArgumentException( "Function '" + property.getName() + "' is not supported" );
        }
        return builder.apply( property.getValue().asData() );
    }

}
