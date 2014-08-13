package com.enonic.wem.api.module;

import org.osgi.framework.Bundle;

public enum ModuleState
{
    UNINSTALLED,
    INSTALLED,
    ACTIVE;

    public static ModuleState fromBundleState( final Bundle bundle )
    {
        switch ( bundle.getState() )
        {
            case Bundle.UNINSTALLED:
                return UNINSTALLED;

            case Bundle.INSTALLED:
            case Bundle.RESOLVED:
            case Bundle.STARTING:
            case Bundle.STOPPING:
                return INSTALLED;

            case Bundle.ACTIVE:
                return ACTIVE;

            default:
                return INSTALLED;
        }
    }
}
