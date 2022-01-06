package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StemmedQueryBuilderTest
    extends QueryBuilderTest
{
    @Test
    public void simple()
        throws Exception
    {
        test( "simple" );
    }

    @Test
    public void multiple_fields()
        throws Exception
    {
        test( "multiple_fields" );
    }

    @Test
    public void operator()
        throws Exception
    {
        test( "operator" );
    }

    @Test
    public void weighted()
        throws Exception
    {
        test( "weighted" );
    }

    @Test
    public void boosted()
        throws Exception
    {
        test( "boosted" );
    }

    @Test
    public void invalid_language()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_language" ) );
    }


    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "stemmed/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new StemmedQueryBuilder( dslExpression.getSet( "stemmed" ) ).create();

        assertJson( "stemmed/result/" + fileName + ".json", builder.toString() );

    }

}
