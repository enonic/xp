package com.enonic.xp.security;

import java.util.stream.Stream;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A ceiling on the roles an authentication holds. {@link #none() None} keeps the user's full
 * memberships; a restriction {@link #to(PrincipalKeys) to} some roles keeps only those of them the
 * user holds. Groups decide which roles the user holds but are never kept themselves. The user's
 * own key and the roles every authenticated user has always stay.
 * <p>
 * A member of {@link RoleKeys#ADMIN system.admin} counts as a member of every role, since that
 * role passes every check in XP. A restriction keeps every role it names for such a user, and
 * {@code system.admin} itself only when named.
 */
@PublicApi
@NullMarked
public sealed interface RoleRestriction
    permits RoleRestriction.None, RoleRestriction.To
{
    /**
     * No restriction: the user's full memberships.
     */
    static RoleRestriction none()
    {
        return new None();
    }

    /**
     * A restriction to the given roles; to none of them when the set is empty.
     *
     * @throws IllegalArgumentException when a key is not a role
     */
    static RoleRestriction to( final PrincipalKeys roles )
    {
        return new To( roles );
    }

    /**
     * A restriction to the given roles; to none of them when there are none.
     *
     * @throws IllegalArgumentException when a key is not a role
     */
    static RoleRestriction to( final PrincipalKey... roles )
    {
        return new To( PrincipalKeys.from( roles ) );
    }

    /**
     * Whether this restriction takes anything away: false for {@link #none()}.
     */
    boolean isRestricted();

    /**
     * The memberships that remain under this restriction, in their order, followed by any named roles
     * a member of {@code system.admin} gains.
     */
    PrincipalKeys apply( PrincipalKeys memberships );

    /**
     * What passes both this restriction and the other: none and a restriction give that restriction;
     * two role sets give the roles they share.
     */
    RoleRestriction narrow( RoleRestriction other );

    /**
     * No restriction.
     */
    record None()
        implements RoleRestriction
    {
        @Override
        public boolean isRestricted()
        {
            return false;
        }

        @Override
        public PrincipalKeys apply( final PrincipalKeys memberships )
        {
            return memberships;
        }

        @Override
        public RoleRestriction narrow( final RoleRestriction other )
        {
            return other;
        }
    }

    /**
     * A restriction to a set of roles.
     *
     * @param roles the roles that may be kept; empty keeps none
     */
    record To(PrincipalKeys roles)
        implements RoleRestriction
    {
        public To
        {
            requireNonNull( roles, "roles is required" );
            for ( final PrincipalKey role : roles )
            {
                checkArgument( role.isRole(), "A restriction may only name roles, not [%s]", role );
            }
        }

        @Override
        public boolean isRestricted()
        {
            return true;
        }

        @Override
        public PrincipalKeys apply( final PrincipalKeys memberships )
        {
            final PrincipalKeys held = named( memberships );
            if ( !memberships.contains( RoleKeys.ADMIN ) )
            {
                return held;
            }
            return Stream.concat( held.stream(), roles.stream().filter( role -> !held.contains( role ) ) ).collect( PrincipalKeys.collector() );
        }

        @Override
        public RoleRestriction narrow( final RoleRestriction other )
        {
            return other instanceof To( PrincipalKeys theirs ) ? new To( named( theirs ) ) : this;
        }

        private PrincipalKeys named( final PrincipalKeys keys )
        {
            return keys.stream().filter( roles::contains ).collect( PrincipalKeys.collector() );
        }
    }
}
