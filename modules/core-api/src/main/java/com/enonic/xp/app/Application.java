package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.module.Module;

@Beta
public final class Application
{
    private final Module module;

    private final ApplicationKey applicationKey;

    public Application( final Module module )
    {
        Preconditions.checkNotNull( module, "Module cannot be null" );
        this.module = module;
        this.applicationKey = ApplicationKey.from( module.getKey().getName() );
    }

    public Module getModule()
    {
        return module;
    }

    public ApplicationKey getKey()
    {
        return applicationKey;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "key", applicationKey ).
            add( "module", module ).
            toString();
    }
}
