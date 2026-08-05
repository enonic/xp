package com.enonic.xp.app;

/**
 * Whiteboard callback for application activation and deactivation.
 *
 * @deprecated No longer invoked — a registered listener will never be called. Track
 * {@link Application} services instead: they are registered while an application is active and
 * unregistered on stop or reconfigure, so a service tracker's {@code addingService} replays every
 * already-active application on open and delivers future ones, and {@code removedService} is
 * delivered inside the unregistration, before any replacement exists. That makes tracking immune to
 * the boot-order and reconfigure-timing races this callback was subject to. Scheduled for removal.
 */
@Deprecated
public interface ApplicationListener
{
    void activated( Application app );

    void deactivated( Application app );
}
