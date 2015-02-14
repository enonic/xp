package com.enonic.xp.core.impl.module;

import java.util.Collection;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

public interface ModuleRegistry
{
    public Module get( ModuleKey key );

    public Collection<Module> getAll();
}
