package com.enonic.xp.app;

/**
 * Whiteboard callback invoked when an application is stopped, uninstalled or reconfigured.
 *
 * @deprecated Error-prone by timing: the callback fires only after the application's service
 * registration is gone — on a reconfigure that is <em>after</em> the replacement has already
 * registered and possibly bootstrapped, so key-based cleanup can destroy the successor's freshly
 * created state instead of the stopped incarnation's. Track {@link Application} services instead:
 * a service tracker's {@code removedService} is delivered inside the unregistration, before any
 * replacement exists. Scheduled for removal.
 */
@Deprecated
public interface ApplicationInvalidator
{
    void invalidate( ApplicationKey key, ApplicationInvalidationLevel level );
}
