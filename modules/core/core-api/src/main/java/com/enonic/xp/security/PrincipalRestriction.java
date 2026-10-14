package com.enonic.xp.security;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

/**
 * A ceiling on the memberships an authentication holds. {@link #none() None} leaves the user's
 * full memberships. A restriction {@link #to(PrincipalKeys) to} a set of principals keeps, of the
 * user's memberships, only those in the set: a listed principal the user does not hold, or no
 * longer holds, is never gained through it, and a restriction to no principals keeps none. The
 * user's own key and the roles every authenticated user has are not memberships and stay in
 * either case.
 */
@PublicApi
@NullMarked
public sealed interface PrincipalRestriction
    permits PrincipalRestriction.None, PrincipalRestriction.To
{
    /**
     * No restriction: the user's full memberships.
     */
    static PrincipalRestriction none()
    {
        return new None();
    }

    /**
     * A restriction to the given principals; to none of them when the set is empty.
     */
    static PrincipalRestriction to( final PrincipalKeys principals )
    {
        return new To( principals );
    }

    /**
     * A restriction to the given principals; to none of them when there are none.
     */
    static PrincipalRestriction to( final PrincipalKey... principals )
    {
        return new To( PrincipalKeys.from( principals ) );
    }

    /**
     * Whether this restriction takes anything away: false for {@link #none()}.
     */
    boolean isRestricted();

    /**
     * The memberships that remain of the given ones under this restriction, in their order.
     */
    PrincipalKeys apply( PrincipalKeys memberships );

    /**
     * No restriction.
     */
    record None()
        implements PrincipalRestriction
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
    }

    /**
     * A restriction to a set of principals.
     *
     * @param principals the principals that may be kept; empty keeps none
     */
    record To(PrincipalKeys principals)
        implements PrincipalRestriction
    {
        public To
        {
            requireNonNull( principals, "principals is required" );
        }

        @Override
        public boolean isRestricted()
        {
            return true;
        }

        @Override
        public PrincipalKeys apply( final PrincipalKeys memberships )
        {
            return memberships.stream().filter( principals::contains ).collect( PrincipalKeys.collector() );
        }
    }
}
