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
            if ( method.getAnnotation( Subscribe.class ) != null )
            {
                return true;
            }
        }

        return false;
    }
}
