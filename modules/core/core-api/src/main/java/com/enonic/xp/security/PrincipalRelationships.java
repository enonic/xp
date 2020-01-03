package com.enonic.xp.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
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
        return new PrincipalRelationships( ImmutableList.of() );
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
