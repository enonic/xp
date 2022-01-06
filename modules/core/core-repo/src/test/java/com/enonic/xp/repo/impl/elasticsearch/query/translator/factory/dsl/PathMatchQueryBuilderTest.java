package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

public class PathMatchQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    public void minimum_match()
        throws Exception
    {
        test( "minimum_match" );
    }

    @Test
    public void empty_path()
        throws Exception
    {
        test( "empty_path" );
    }

    @Test
    public void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "pathMatch/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new PathMatchQueryBuilder( dslExpression.getSet( "pathMatch" ) ).create();

        assertJson( "pathMatch/result/" + fileName + ".json", builder.toString() );

    }

}
