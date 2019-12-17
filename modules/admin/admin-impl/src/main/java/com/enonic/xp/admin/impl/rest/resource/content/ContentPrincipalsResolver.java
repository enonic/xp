package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

public final class ContentPrincipalsResolver
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentPrincipalsResolver.class );

    private final SecurityService securityService;

    public ContentPrincipalsResolver( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public Principals resolveAccessControlListPrincipals( final AccessControlList... accessControlList )
    {
        final Map<PrincipalKey, Principal> principals = new HashMap<>();
        for ( AccessControlList acl : accessControlList )
        {
            if ( acl != null )
            {
                findPrincipals( principals, acl );
            }
        }
        return Principals.from( principals.values() );
    }

    private void findPrincipals( final Map<PrincipalKey, Principal> principals, final AccessControlList acl )
    {
        for ( AccessControlEntry entry : acl )
        {
            final PrincipalKey key = entry.getPrincipal();
            if ( !principals.containsKey( key ) )
            {
                final Optional<? extends Principal> principalValue = securityService.getPrincipal( key );
                if ( !principalValue.isPresent() )
                {
                    LOG.warn( "Principal could not be resolved: " + key.toString() );
                }
                else
                {
                    principals.put( key, principalValue.get() );
                }
            }
        }
    }

    public Principal findPrincipal( final PrincipalKey key )
    {
        final Optional<? extends Principal> principalValue = securityService.getPrincipal( key );
        if ( !principalValue.isPresent() )
        {
            LOG.warn( "Principal could not be resolved: " + key.toString() );
            return null;
        }
        else
        {
            return principalValue.get();
        }
    }

    public Principals findPrincipals( final PrincipalKeys keys )
    {
        return securityService.getPrincipals( keys );
    }
}
