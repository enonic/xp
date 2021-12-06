package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

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
    public void invalid_param()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_param" ) );
    }


    @Test
    public void invalid_inner_param()
        throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> test( "invalid_inner_param" ) );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "boolean/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new BooleanQueryBuilder( dslExpression.getSet( "boolean" ) ).create();

        assertJson( "boolean/result/" + fileName + ".json", builder.toString() );

    }

}
