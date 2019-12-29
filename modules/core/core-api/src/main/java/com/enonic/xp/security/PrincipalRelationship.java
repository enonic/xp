package com.enonic.xp.security;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class PrincipalRelationship
{
    private final PrincipalKey from;

    private final PrincipalKey to;

    private PrincipalRelationship( final Builder builder )
    {
        checkNotNull( builder.from, "Principal relationship 'from' cannot be null" );
        checkNotNull( builder.to, "Principal relationship 'to' cannot be null" );
        checkArgument( !builder.from.equals( builder.to ), "Principal relationship 'from' and 'to' cannot refer to the same principal" );
        checkArgument( !( builder.from.isRole() && builder.to.isRole() ), "Principal relationship from Role to Role is not allowed" );
        checkArgument( !( builder.from.isGroup() && builder.to.isRole() ), "Principal relationship from Group to Role is not allowed" );
        checkArgument( !builder.from.isUser(), "Principal relationship from User to another Principal is not allowed" );

        this.from = builder.from;
        this.to = builder.to;
    }

    public PrincipalKey getFrom()
    {
        return from;
    }

    public PrincipalKey getTo()
    {
        return to;
    }

    @Override
    public String toString()
    {
        return from.toString() + " -> " + to.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PrincipalRelationship ) )
        {
            return false;
        }
        final PrincipalRelationship that = (PrincipalRelationship) o;
        return Objects.equals( this.to, that.to ) && Objects.equals( this.from, that.from );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, to );
    }

    public static Builder from( final PrincipalKey fromPrincipal )
    {
        return new Builder( fromPrincipal );
    }

    public static class Builder
    {
        private final PrincipalKey from;

        private PrincipalKey to;

        private Builder( final PrincipalKey from )
        {
            this.from = from;
        }

        public PrincipalRelationship to( final PrincipalKey toPrincipal )
        {
            this.to = toPrincipal;
            return new PrincipalRelationship( this );
        }
    }

}
