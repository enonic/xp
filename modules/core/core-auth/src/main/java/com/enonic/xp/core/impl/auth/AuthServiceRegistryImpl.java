package com.enonic.xp.core.impl.auth;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.auth.AuthService;

@Component(immediate = true)
public class AuthServiceRegistryImpl
{
    private final Map<String, AuthService> authServiceMap = Maps.newConcurrentMap();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addAuthService( AuthService authService )
    {
        authServiceMap.put( authService.getName(), authService );
    }

    public void removeAuthService( AuthService authService )
    {
        authServiceMap.remove( authService.getName() );
    }
}
