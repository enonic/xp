package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

class MatchAllQueryBuilderTest
    extends QueryBuilderTest
{

    @Test
    void match_all()
        throws Exception
    {
        test( "match_all" );
    }

    @Test
    void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "matchAll/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new MatchAllQueryBuilder( dslExpression.getSet( "matchAll" ) ).create();

        assertJson( "matchAll/result/" + fileName + ".json", builder.toString() );
    }
}
