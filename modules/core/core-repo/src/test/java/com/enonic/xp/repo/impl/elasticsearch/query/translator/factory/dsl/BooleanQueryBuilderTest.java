package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;


class BooleanQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    void must()
        throws Exception
    {
        test( "must" );
    }

    @Test
    void mustNot()
        throws Exception
    {
        test( "mustNot" );
    }

    @Test
    void mustNotArray()
        throws Exception
    {
        test( "mustNot_array" );
    }

    @Test
    void should()
        throws Exception
    {
        test( "should" );
    }

    @Test
    void filter()
        throws Exception
    {
        test( "filter" );
    }

    @Test
    void multiple()
        throws Exception
    {
        test( "multiple" );
    }

    @Test
    void inner()
        throws Exception
    {
        test( "inner" );
    }

    @Test
    void inherited()
        throws Exception
    {
        test( "inherited" );
    }

    @Test
    void inner_boosted_boolean()
        throws Exception
    {
        test( "inner_boosted_boolean" );
    }

    @Test
    void invalid_param()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_param" ) );
    }

    @Test
    void invalid_property_type()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_property_type" ) );
    }


    @Test
    void invalid_inner_param()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_inner_param" ) );
    }

    @Test
    void invalid_numeric_property()
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_numeric_property" ) );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "boolean/query/" + fileName + ".json" );

        final PropertyTree dslExpression = readJson( queryString );
        final QueryBuilder builder = new BooleanQueryBuilder( dslExpression.getSet( "boolean" ) ).create();

        assertJson( "boolean/result/" + fileName + ".json", builder.toString() );

    }

}
