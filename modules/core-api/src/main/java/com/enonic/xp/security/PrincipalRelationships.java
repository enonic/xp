package com.enonic.xp.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class PrincipalRelationships
    extends AbstractImmutableEntityList<PrincipalRelationship>
{
    private PrincipalRelationships( final ImmutableList<PrincipalRelationship> list )
    {
        super( list );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static PrincipalRelationships empty()
    {
        final ImmutableList<PrincipalRelationship> list = ImmutableList.of();
        return new PrincipalRelationships( list );
    }

    public static PrincipalRelationships from( final PrincipalRelationship... principalRelationships )
    {
        return new PrincipalRelationships( ImmutableList.copyOf( principalRelationships ) );
    }

    public static PrincipalRelationships from( final Iterable<? extends PrincipalRelationship> principalRelationships )
    {
        return new PrincipalRelationships( ImmutableList.copyOf( principalRelationships ) );
    }
}
