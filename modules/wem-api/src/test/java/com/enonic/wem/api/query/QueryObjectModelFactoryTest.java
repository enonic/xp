package com.enonic.wem.api.query;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryObjectModelFactoryTest
{
    private QueryObjectModelFactory factory;

    @Before
    public void setUp()
    {
        factory = new QueryObjectModelFactory();
    }

    @Test
    public void like()
    {
        final Comparison like = factory.like( "fisk", "ost" );

        assertEquals( "fisk LIKE \"ost\"", like.toString() );
    }

    @Test
    public void equalTo()
    {
        final Comparison equalTo = factory.equalTo( "fisk", "ost" );

        assertEquals( "fisk = \"ost\"", equalTo.toString() );
    }

    @Test
    public void and()
    {
        final And and = factory.and( factory.like( "fisk", "ost" ), factory.like( "sild", "pels" ) );

        assertEquals( "fisk LIKE \"ost\" AND sild LIKE \"pels\"", and.toString() );
    }

    @Test
    public void or()
    {
        final Or or = factory.or( factory.like( "fisk", "ost" ), factory.like( "sild", "pels" ) );

        assertEquals( "fisk LIKE \"ost\" OR sild LIKE \"pels\"", or.toString() );
    }

    @Test
    public void not()
    {
        final Not not = factory.not( factory.equalTo( "fisk", 2 ) );

        assertEquals( "NOT ( fisk = 2 )", not.toString() );
    }

    @Test
    public void lessThan()
    {
        final Comparison lessThan = factory.lessThan( "fisk", 3 );

        assertEquals( "fisk < 3", lessThan.toString() );
    }

    @Test
    public void greaterThan()
    {
        final Comparison greaterThan = factory.greaterThan( "fisk", 2 );

        assertEquals( "fisk > 2", greaterThan.toString() );
    }

    @Test
    public void greaterThanOrEqualTo()
    {
        final Comparison greaterThanOrEqualTo = factory.greaterThanOrEqualTo( "fisk", 2 );

        assertEquals( "fisk >= 2", greaterThanOrEqualTo.toString() );
    }

}

