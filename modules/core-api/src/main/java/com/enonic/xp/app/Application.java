package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.module.Module;

@Beta
public final class Application
{
    private final Module module;

    private final ApplicationKey appKey;

    public Application( final Module module )
    {
        Preconditions.checkNotNull( module, "Module cannot be null" );
        this.module = module;
        this.appKey = ApplicationKey.from( module.getKey() );
    }

    public Module getModule()
    {
        return module;
    }

    public ApplicationKey getKey()
    {
        return appKey;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "key", appKey ).
            add( "module", module ).
            toString();
    }
}
