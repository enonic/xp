package com.enonic.xp.app;

/**
 * Whiteboard callback invoked when an application is activated or deactivated.
 *
 * @deprecated Misses events by registration order: dispatch reaches only the listeners registered at
 * the time, and there is no replay, so a listener whose bundle starts after an application is
 * already active never learns about it. Track {@link Application} services instead: they are
 * registered while an application is active and unregistered on stop or reconfigure, so a service
 * tracker's {@code addingService} replays every already-active application on open and delivers
 * future ones, and {@code removedService} is delivered inside the unregistration, before any
 * replacement exists. Scheduled for removal.
 */
@Deprecated
public interface ApplicationListener
{
    void activated( Application app );

    void deactivated( Application app );
}
