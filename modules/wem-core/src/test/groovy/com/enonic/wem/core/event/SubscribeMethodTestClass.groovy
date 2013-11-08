package com.enonic.wem.core.event

import com.google.common.eventbus.Subscribe

class SubscribeMethodTestClass extends NoSubscribeMethodTestClass
{
    @Subscribe
    @Override
    def handleEvent( String event )
    {
        super.handleEvent( event )
    }
}
