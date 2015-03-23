package com.enonic.xp.module;

public interface ModuleService
{
    Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    Modules getModules( ModuleKeys keys );

    Modules getAllModules();

    void installModule( String url );

    void startModule( ModuleKey key );

    void stopModule( ModuleKey key );

    void updateModule( ModuleKey key );

    void uninstallModule( ModuleKey key );
}
