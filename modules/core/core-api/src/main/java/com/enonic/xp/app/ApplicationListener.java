package com.enonic.xp.app;

/**
 * Callback invoked when an application is activated or deactivated.
 *
 * @deprecated No longer invoked — XP does not dispatch to registered listeners anymore. To clean up
 * when an application stops, register a disposer during its bootstrap with {@code __.disposer(fn)};
 * XP runs it while tearing the application's script runtime down. There is currently no Java-level
 * equivalent. Scheduled for removal.
 */
@Deprecated
public interface ApplicationListener
{
    void activated( Application app );

    void deactivated( Application app );
}
