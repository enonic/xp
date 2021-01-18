package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;

public interface ApplicationFactoryService
{
    ApplicationAdaptor getApplication( Bundle bundle );

    Optional<ApplicationAdaptor> findActiveApplication( ApplicationKey applicationKey );
}
