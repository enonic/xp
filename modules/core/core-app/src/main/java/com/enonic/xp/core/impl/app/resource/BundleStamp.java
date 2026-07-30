package com.enonic.xp.core.impl.app.resource;

import org.osgi.framework.Bundle;

/**
 * Identity of an application bundle incarnation. A reinstalled or updated application gets a new stamp,
 * while stop/start of the same bundle keeps it - so cached values keyed by the stamp survive an
 * application restart but never outlive the bundle they were computed from.
 */
record BundleStamp(long bundleId, long lastModified)
{
    static BundleStamp from( final Bundle bundle )
    {
        return new BundleStamp( bundle.getBundleId(), bundle.getLastModified() );
    }
}
