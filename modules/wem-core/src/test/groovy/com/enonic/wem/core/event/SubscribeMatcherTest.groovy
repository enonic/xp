package com.enonic.wem.core.event

import com.google.inject.TypeLiteral
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubscribeMatcherTest extends Specification
{
    def "test matcher that #comment"( )
    {
        def matcher = new SubscribeMatcher()

        expect:
        flag == matcher.matches( TypeLiteral.get( clz ) )

        where:
        flag  | clz                        | comment
        false | NoSubscribeMethodTestClass | "does not match"
        true  | SubscribeMethodTestClass   | "matches"
    }
}
