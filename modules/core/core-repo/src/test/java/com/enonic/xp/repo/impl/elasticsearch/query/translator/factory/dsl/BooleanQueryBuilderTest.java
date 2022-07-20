package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class BooleanQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void must()
        throws Exception
    {
        test( "must" );
    }

    @Test
    public void mustNot()
        throws Exception
    {
        test( "mustNot" );
    }

    @Test
    public void should()
        throws Exception
    {
        test( "should" );
    }

    @Test
    public void filter()
        throws Exception
    {
        test( "filter" );
    }

    @Test
    public void multiple()
        throws Exception
    {
        test( "multiple" );
    }

    @Test
    public void inner()
        throws Exception
    {
        test( "inner" );
    }

    @Test
    public void inherited()
        throws Exception
    {
        test( "inherited" );
    }

    @Test
    public void inner_boosted_boolean()
        throws Exception
    {
        test( "inner_boosted_boolean" );
    }

    @Test
    public void invalid_param()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_param" ) );
    }

    @Test
    public void invalid_property_type()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_property_type" ) );
    }


    @Test
    public void invalid_inner_param()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_inner_param" ) );
    }

    @Test
    public void invalid_numeric_property()
        throws Exception
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
