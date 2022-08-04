package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExistsQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void empty_field()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "empty_field" ) );
    }

    @Test
    public void null_field()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "null_field" ) );
    }

    @Test
    public void number()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "number" ) );
    }

    @Test
    public void simple()
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
