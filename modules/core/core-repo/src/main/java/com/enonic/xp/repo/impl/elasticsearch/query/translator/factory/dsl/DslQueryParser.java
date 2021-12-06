package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.Map;
import java.util.function.Function;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

public class DslQueryParser
{
    private static final Map<String, Function<PropertySet, QueryBuilder>> QUERY_BUILDERS =
        Map.of( BooleanQueryBuilder.NAME, ( set -> new BooleanQueryBuilder( set ).create() ), TermQueryBuilder.NAME,
                ( set -> new TermQueryBuilder( set ).create() ), InQueryBuilder.NAME, ( set -> new InQueryBuilder( set ).create() ),
                LikeQueryBuilder.NAME, ( set -> new LikeQueryBuilder( set ).create() ), FulltextQueryBuilder.NAME,
                ( set -> new FulltextQueryBuilder( set ).create() ), NgramQueryBuilder.NAME,
                ( set -> new NgramQueryBuilder( set ).create() ), StemmedQueryBuilder.NAME,
                ( set -> new StemmedQueryBuilder( set ).create() ), RangeQueryBuilder.NAME,
                ( set -> new RangeQueryBuilder( set ).create() ), PathMatchQueryBuilder.NAME,
                ( set -> new PathMatchQueryBuilder( set ).create() ) );

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
