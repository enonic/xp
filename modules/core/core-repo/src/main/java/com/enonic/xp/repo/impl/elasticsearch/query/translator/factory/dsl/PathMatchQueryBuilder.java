package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class PathMatchQueryBuilder
    extends DslQueryBuilder
{
    public static final String NAME = "pathMatch";

    private final String field;

    private final String path;

    private final Long minimumMatch;

    PathMatchQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.field = getString( "field" );
        this.path = getString( "path" );
        this.minimumMatch = getLong( "minimumMatch", 1L );
    }

    public QueryBuilder create()
    {
        final MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder( getFieldName(), path );

        if ( minimumMatch > 1 )
        {
            final String minimumPath = Arrays.stream( path.split( "/" ) ).limit( minimumMatch + 1 ).collect( Collectors.joining( "/" ) );

            final TermQueryBuilder termQueryBuilder = new TermQueryBuilder( getFieldName(), minimumPath );

            return new BoolQueryBuilder().must( termQueryBuilder ).must( matchQueryBuilder );
        }

        return addBoost( matchQueryBuilder, boost );
    }

    private String getFieldName()
    {
        return SearchQueryFieldNameResolver.INSTANCE.resolve( field, IndexValueType.PATH );
    }
}
