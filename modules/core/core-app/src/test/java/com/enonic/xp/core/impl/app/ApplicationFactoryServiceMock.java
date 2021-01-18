package com.enonic.xp.core.impl.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.server.RunMode;

public class ApplicationFactoryServiceMock
    implements ApplicationFactoryService
{
    private final ApplicationFactory factory = new ApplicationFactory( RunMode.get() );

    private final Map<Bundle, ApplicationAdaptor> map = new HashMap<>();

    @Override
    public ApplicationAdaptor getApplication( final Bundle bundle )
    {
        return map.computeIfAbsent( bundle, b -> factory.create( b ) );
    }

    @Override
    public Optional<ApplicationAdaptor> findActiveApplication( final ApplicationKey applicationKey )
    {
        return map.entrySet().
            stream().
            filter( e -> e.getKey().getSymbolicName().equals( applicationKey.getName() ) ).
            map( e -> e.getValue() ).
            findAny();
    }
}
