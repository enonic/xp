package com.enonic.wem.core.event

class NoSubscribeMethodTestClass
{
    def event

    def handleEvent( String event )
    {
        this.event = event
    }
}
