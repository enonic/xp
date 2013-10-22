package com.enonic.wem.core.lifecycle;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.spi.DefaultBindingScopingVisitor;

final class SingletonScopeVisitor
    extends DefaultBindingScopingVisitor<Boolean>
{
    @Override
    public Boolean visitEagerSingleton()
    {
        return true;
    }

    @Override
    public Boolean visitScope( final Scope scope )
    {
        return scope != null && scope == Scopes.SINGLETON;
    }
}
