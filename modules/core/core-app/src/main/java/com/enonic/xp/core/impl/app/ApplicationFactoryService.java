package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

public interface ApplicationFactoryService
{
    ApplicationAdaptor getApplication( Bundle bundle );

    Optional<ApplicationAdaptor> findActiveApplication( ApplicationKey applicationKey );

    Optional<ApplicationUrlResolver> findResolver( ApplicationKey applicationKey, String resolverSource );
}
