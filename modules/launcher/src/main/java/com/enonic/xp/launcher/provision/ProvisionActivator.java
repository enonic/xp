package com.enonic.xp.launcher.provision;

/**
 * New initial provisioning...
 *
 * If in "prod" mode:
 * - Load bundles from XP_INSTALL/system.
 *
 * If in "dev" mode:
 * - Needs project root folder set.
 * - Resolve bundles starting with specific group id form project.
 * - Else from XP_INSTALL/system
 */
public final class ProvisionActivator
{
}
