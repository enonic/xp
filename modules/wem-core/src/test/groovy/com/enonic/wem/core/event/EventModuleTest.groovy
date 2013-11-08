package com.enonic.wem.core.event

import com.google.common.eventbus.EventBus
import com.google.inject.Guice
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class EventModuleTest extends Specification
{
    def "event is #comment"( )
    {
        def injector = Guice.createInjector( new EventModule() )
        def eventBus = injector.getInstance( EventBus )
        def eventListener = injector.getInstance( SubscribeMethodTestClass )

        when:
        eventBus.post( event )

        then:
        expected == eventListener.event

        where:
        event        | expected | comment
        "hello"      | "hello"  | "dispatched"
        new Object() | null     | "not dispatched"
    }
}
