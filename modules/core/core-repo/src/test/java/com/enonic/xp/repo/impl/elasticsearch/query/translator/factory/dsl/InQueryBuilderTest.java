package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class InQueryBuilderTest
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
    public void one()
        throws Exception
    {
        test( "one" );
    }

    @Test
    public void multiple()
        throws Exception
    {
        test( "multiple" );
    }

    @Test
    public void with_geopoint()
        throws Exception
    {
        test( "with_geopoint" );
    }

    @Test
    public void with_number()
        throws Exception
    {
        test( "with_number" );
    }

    @Test
    public void with_dateTime()
        throws Exception
    {
        test( "with_dateTime" );
    }

    @Test
    public void with_date()
        throws Exception
    {
        test( "with_date" );
    }

    @Test
    public void with_time()
        throws Exception
    {
        test( "with_time" );
    }

    private void test( final String fileName )
        throws Exception
    {
        final String queryString = load( "in/query/" + fileName + ".json" );

        final PropertyTree dslExpression = JsonToPropertyTreeTranslator.translate( JsonHelper.from( queryString ) );
        final QueryBuilder builder = new InQueryBuilder( dslExpression.getSet( "in" ) ).create();

        assertJson( "in/result/" + fileName + ".json", builder.toString() );

    }

}
