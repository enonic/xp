package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExistsQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void empty_field()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "empty_field" ) );
    }

    @Test
    void null_field()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "null_field" ) );
    }

    @Test
    void number()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "number" ) );
    }

    @Test
    void simple()
        throws Exception
    {
        test( "simple" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "exists/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new ExistsQueryBuilder( dslExpression.getSet( "exists" ) ).create();

        assertJson( "exists/result/" + fileName + ".json", builder.toString() );

    }

}
