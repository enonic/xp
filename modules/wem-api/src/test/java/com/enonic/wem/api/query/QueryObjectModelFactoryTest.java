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

        System.out.println( "fisk LIKE ('ost')" );

    }

    @Test
    public void and()
    {
        final Constraint and = factory.and( factory.like( "fisk", "ost" ), factory.like( "nebbsild", "tufs" ) );

        assertEquals( "fisk LIKE ('ost') AND nebbsild LIKE ('tufs')", and.toString() );
    }

}

