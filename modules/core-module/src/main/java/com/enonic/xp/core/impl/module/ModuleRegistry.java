package com.enonic.xp.core.impl.module;

import java.util.Collection;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.module.Module;

public interface ModuleRegistry
{
    Module get( ApplicationKey key );

    Collection<Module> getAll();
}
