package com.enonic.xp.app;

@Deprecated
public interface ApplicationListener
{
    void activated( Application app );

    void deactivated( Application app );
}
