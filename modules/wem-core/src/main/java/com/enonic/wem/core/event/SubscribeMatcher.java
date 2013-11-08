package com.enonic.wem.core.event;

import java.lang.reflect.Method;

import com.google.common.eventbus.Subscribe;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;

final class SubscribeMatcher
    extends AbstractMatcher<TypeLiteral<?>>
{
    @Override
    public boolean matches( final TypeLiteral<?> typeLiteral )
    {
        for ( final Method method : typeLiteral.getRawType().getMethods() )
        {
            if ( isSubscribeMethod( method ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean isSubscribeMethod( final Method method )
    {
        return method.getAnnotation( Subscribe.class ) != null;
    }
}
