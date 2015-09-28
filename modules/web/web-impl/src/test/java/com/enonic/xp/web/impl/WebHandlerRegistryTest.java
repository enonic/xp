package com.enonic.xp.web.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.web.handler.WebHandler;

public class WebHandlerRegistryTest
{
    private WebHandlerRegistry registry;

    @Before
    public void setup()
    {
        this.registry = new WebHandlerRegistry();
    }

    @Test
    public void testAddRemove()
    {
        final TestWebHandler handler = new TestWebHandler( 0, false );

        Assert.assertEquals( 0, this.registry.getList().size() );

        this.registry.add( handler );
        Assert.assertEquals( 1, this.registry.getList().size() );

        this.registry.add( handler );
        Assert.assertEquals( 1, this.registry.getList().size() );

        this.registry.remove( handler );
        Assert.assertEquals( 0, this.registry.getList().size() );
    }

    @Test
    public void testOrder()
    {
        assertList( this.registry.getList() );

        final TestWebHandler handler1 = new TestWebHandler( 0, false );
        this.registry.add( handler1 );
        assertList( this.registry.getList(), handler1 );

        final TestWebHandler handler2 = new TestWebHandler( WebHandler.MIN_ORDER, false );
        this.registry.add( handler2 );
        assertList( this.registry.getList(), handler2, handler1 );

        final TestWebHandler handler3 = new TestWebHandler( WebHandler.MAX_ORDER, false );
        this.registry.add( handler3 );
        assertList( this.registry.getList(), handler2, handler1, handler3 );

        final TestWebHandler handler4 = new TestWebHandler( 10, false );
        this.registry.add( handler4 );
        assertList( this.registry.getList(), handler2, handler1, handler4, handler3 );

        this.registry.remove( handler1 );
        this.registry.remove( handler2 );
        assertList( this.registry.getList(), handler4, handler3 );
    }

    private void assertList( final List<WebHandler> actual, final WebHandler... expected )
    {
        Assert.assertEquals( expected.length, actual.size() );

        for ( int i = 0; i < expected.length; i++ )
        {
            Assert.assertSame( "Differs at index " + i, actual.get( i ), expected[i] );
        }
    }
}
