package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

public class TermQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    public void number()
        throws Exception
    {
        test( "number" );
    }

    @Test
    public void test_boolean()
        throws Exception
    {
        test( "boolean" );
    }

    @Test
    public void boost()
        throws Exception
    {
        test( "boost" );
    }

    @Test
    public void empty_value()
        throws Exception
    {
        test( "empty_value" );
    }

    @Test
    public void null_value()
        throws Exception
    {
        test( "null_value" );
    }

    @Test
    public void datetime_as_number()
        throws Exception
    {
        test( "datetime_as_number" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "term/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new TermQueryBuilder( dslExpression.getSet( "term" ) ).create();

        assertJson( "term/result/" + fileName + ".json", builder.toString() );

    }

}
