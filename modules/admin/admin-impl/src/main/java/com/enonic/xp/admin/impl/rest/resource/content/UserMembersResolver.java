package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.SecurityService;

import static java.util.Objects.requireNonNull;

final class UserMembersResolver
{
    private final SecurityService securityService;

    private Map<PrincipalKey, PrincipalKeys> membersCache;

    public UserMembersResolver( final SecurityService securityService )
    {
        this.securityService = requireNonNull( securityService );
        this.membersCache = new HashMap<>();
    }

    public PrincipalKeys getUserMembers( final PrincipalKey principal )
    {
        final PrincipalKeys cachedValue = this.membersCache.get( principal );
        if ( cachedValue != null )
        {
            return cachedValue;
        }

        final ImmutableSet.Builder<PrincipalKey> members = ImmutableSet.builder();
        doGetUserMembers( members, principal );

        final PrincipalKeys membersResult = PrincipalKeys.from( members.build() );
        this.membersCache.put( principal, membersResult );
        return membersResult;
    }

    private void doGetUserMembers( final ImmutableSet.Builder<PrincipalKey> members, final PrincipalKey principal )
    {
        final PrincipalKeys newMembers = this.getMembers( principal );
        members.add( newMembers.stream().filter( PrincipalKey::isUser ).toArray( PrincipalKey[]::new ) );

        for ( PrincipalKey member : newMembers )
        {
            if ( !member.isUser() )
            {
                doGetUserMembers( members, member );
            }
        }
    }

    private PrincipalKeys getMembers( final PrincipalKey principal )
    {
        final PrincipalKeys cachedValue = this.membersCache.get( principal );
        if ( cachedValue != null )
        {
            return cachedValue;
        }

        final PrincipalRelationships relationships = this.securityService.getRelationships( principal );
        final ImmutableSet.Builder<PrincipalKey> memberSet = ImmutableSet.builder();
        for ( PrincipalRelationship relationship : relationships )
        {
            memberSet.add( relationship.getTo() );
        }
        final PrincipalKeys members = PrincipalKeys.from( memberSet.build() );

        this.membersCache.put( principal, members );
        return members;
    }

}
