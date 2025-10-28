package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LikeQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void null_value()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "null_value" ) );
    }

    @Test
    void empty()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "empty" ) );
    }

    @Test
    void both()
        throws Exception
    {
        test( "both" );
    }

    @Test
    void inside()
        throws Exception
    {
        test( "inside" );
    }

    @Test
    void left()
        throws Exception
    {
        test( "left" );
    }

    @Test
    void right()
        throws Exception
    {
        test( "right" );
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
        final String queryString = load( "like/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new LikeQueryBuilder( dslExpression.getSet( "like" ) ).create();

        assertJson( "like/result/" + fileName + ".json", builder.toString() );
    }
}
