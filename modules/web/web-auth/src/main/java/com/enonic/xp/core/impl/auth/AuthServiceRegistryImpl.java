package com.enonic.xp.core.impl.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.web.auth.AuthService;
import com.enonic.xp.web.auth.AuthServiceRegistry;

@Component(immediate = true)
public class AuthServiceRegistryImpl
    implements AuthServiceRegistry
{
    private final Map<String, AuthService> authServiceMap = Maps.newConcurrentMap();

    @Override
    public Collection<AuthService> getAuthServices()
    {
        return Collections.unmodifiableCollection( authServiceMap.values() );
    }

    @Override
    public AuthService getAuthService( final String key )
    {
        return authServiceMap.get( key );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addAuthService( AuthService authService )
    {
        authServiceMap.put( authService.getKey(), authService );
    }

    public void removeAuthService( AuthService authService )
    {
        authServiceMap.remove( authService.getKey() );
    }
}
