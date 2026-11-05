package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;

public interface ApplicationFactoryService
{
    ApplicationAdaptor getApplication( Bundle bundle );

    Optional<ApplicationAdaptor> findActiveApplication( ApplicationKey applicationKey );

    /**
     * The installed bundle of the application, whatever its state, if any.
     */
    Optional<Bundle> findBundle( ApplicationKey applicationKey );

    /**
     * The active bundle of the application and the resolver serving its resources, of one lookup. With a {@code resolverSource}
     * ({@code bundle}), the resolver over that source of the active bundle alone; without one, the resolver of the active
     * application, else the persisted descriptor and schema of an installed or merely persisted application.
     */
    ResourceSource findResourceSource( ApplicationKey applicationKey, String resolverSource );
}
