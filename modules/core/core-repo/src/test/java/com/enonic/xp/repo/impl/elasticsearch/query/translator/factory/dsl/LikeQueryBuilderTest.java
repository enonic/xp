package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LikeQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void null_value()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "null_value" ) );
    }

    @Test
    public void empty()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "empty" ) );
    }

    @Test
    public void both()
        throws Exception
    {
        test( "both" );
    }

    @Test
    public void inside()
        throws Exception
    {
        test( "inside" );
    }

    @Test
    public void left()
        throws Exception
    {
        test( "left" );
    }

    @Test
    public void right()
        throws Exception
    {
        test( "right" );
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
        final String queryString = load( "like/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new LikeQueryBuilder( dslExpression.getSet( "like" ) ).create();

        assertJson( "like/result/" + fileName + ".json", builder.toString() );
    }
}
