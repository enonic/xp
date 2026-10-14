package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrincipalRestrictionTest
{
    private static final PrincipalKey GROUP = PrincipalKey.ofGroup( IdProviderKey.system(), "editors" );

    private static final PrincipalKeys MEMBERSHIPS = PrincipalKeys.from( RoleKeys.ADMIN_LOGIN, GROUP, RoleKeys.CONTENT_MANAGER_APP );

    @Test
    void none_keeps_everything()
    {
        assertThat( PrincipalRestriction.none().isRestricted() ).isFalse();
        assertThat( PrincipalRestriction.none().apply( MEMBERSHIPS ) ).isEqualTo( MEMBERSHIPS );
        assertThat( PrincipalRestriction.none() ).isEqualTo( PrincipalRestriction.none() ).isNotEqualTo( PrincipalRestriction.to() );
    }

    @Test
    void to_keeps_only_held_principals_among_the_listed_in_membership_order()
    {
        final PrincipalRestriction restriction = PrincipalRestriction.to( RoleKeys.CONTENT_MANAGER_APP, RoleKeys.ADMIN, RoleKeys.ADMIN_LOGIN );
        assertThat( restriction.isRestricted() ).isTrue();
        assertThat( restriction.apply( MEMBERSHIPS ) ).containsExactly( RoleKeys.ADMIN_LOGIN, RoleKeys.CONTENT_MANAGER_APP );
        assertThat( restriction ).isEqualTo( PrincipalRestriction.to( PrincipalKeys.from( RoleKeys.CONTENT_MANAGER_APP, RoleKeys.ADMIN, RoleKeys.ADMIN_LOGIN ) ) );
    }

    @Test
    void to_nothing_keeps_nothing_and_is_not_none()
    {
        final PrincipalRestriction nothing = PrincipalRestriction.to();
        assertThat( nothing.isRestricted() ).isTrue();
        assertThat( nothing.apply( MEMBERSHIPS ) ).isEmpty();
        assertThat( nothing ).isEqualTo( PrincipalRestriction.to( PrincipalKeys.empty() ) ).isNotEqualTo( PrincipalRestriction.none() );
        assertThatThrownBy( () -> PrincipalRestriction.to( (PrincipalKeys) null ) ).isInstanceOf( NullPointerException.class );
    }
}
