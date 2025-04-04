package com.enonic.xp.web.impl.handler;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.handler.WebHandler;

import static org.junit.jupiter.api.Assertions.assertSame;

class WebDispatcherImplTest
{
    @Test
    void orderedProperly()
    {
        final WebDispatcherImpl dispatcher = new WebDispatcherImpl();
        TestWebHandler webHandlerMin = new TestWebHandler( WebHandler.MIN_ORDER );
        dispatcher.add( webHandlerMin );
        TestWebHandler webHandlerMax = new TestWebHandler( WebHandler.MAX_ORDER );
        dispatcher.add( webHandlerMax );
        TestWebHandler webHandler0 = new TestWebHandler( 0 );
        dispatcher.add( webHandler0 );
        TestWebHandler webHandler1 = new TestWebHandler( 1 );
        dispatcher.add( webHandler1 );

        List<WebHandler> list = dispatcher.list();
        assertSame( webHandlerMin, list.get( 0 ) );
        assertSame( webHandler0, list.get( 1 ) );
        assertSame( webHandler1, list.get( 2 ) );
        assertSame( webHandlerMax, list.get( 3 ) );
    }

    @Test
    void supportsEqualOderElements()
    {
        final WebDispatcherImpl dispatcher = new WebDispatcherImpl();
        TestWebHandler webHandler0 = new TestWebHandler( 0 );
        dispatcher.add( webHandler0 );
        TestWebHandler webHandlerAlso0 = new TestWebHandler( 0 );
        dispatcher.add( webHandlerAlso0 );

        List<WebHandler> list = dispatcher.list();
        assertSame( webHandler0, list.get( 0 ) );
        assertSame( webHandlerAlso0, list.get( 1 ) );
    }
}
