package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleRestrictionTest
{
    private static final PrincipalKey GROUP = PrincipalKey.ofGroup( IdProviderKey.system(), "editors" );

    private static final PrincipalKey EDITOR = PrincipalKey.ofRole( "cms.project.site.editor" );

    private static final PrincipalKeys MEMBERSHIPS = PrincipalKeys.from( RoleKeys.ADMIN_LOGIN, GROUP, EDITOR );

    @Test
    void none_keeps_everything()
    {
        assertThat( RoleRestriction.none().isRestricted() ).isFalse();
        assertThat( RoleRestriction.none().apply( MEMBERSHIPS ) ).isEqualTo( MEMBERSHIPS );
        assertThat( RoleRestriction.none() ).isEqualTo( RoleRestriction.none() ).isNotEqualTo( RoleRestriction.to() );
    }

    @Test
    void to_keeps_only_held_roles_among_the_listed_in_membership_order()
    {
        final RoleRestriction restriction = RoleRestriction.to( EDITOR, RoleKeys.ADMIN, RoleKeys.ADMIN_LOGIN );
        assertThat( restriction.isRestricted() ).isTrue();
        assertThat( restriction.apply( MEMBERSHIPS ) ).as( "the group is never kept" ).containsExactly( RoleKeys.ADMIN_LOGIN, EDITOR );
        assertThat( restriction ).isEqualTo( RoleRestriction.to( PrincipalKeys.from( EDITOR, RoleKeys.ADMIN, RoleKeys.ADMIN_LOGIN ) ) );
    }

    @Test
    void only_roles_may_be_named()
    {
        assertThatThrownBy( () -> RoleRestriction.to( RoleKeys.ADMIN_LOGIN, GROUP ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( GROUP.toString() );
        assertThatThrownBy( () -> RoleRestriction.to( PrincipalKey.ofUser( IdProviderKey.system(), "alice" ) ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void an_administrator_is_a_member_of_every_role()
    {
        final PrincipalKey market = PrincipalKey.ofRole( "cms.project.market.editor" );
        final PrincipalKeys admin = PrincipalKeys.from( RoleKeys.ADMIN, RoleKeys.ADMIN_LOGIN, GROUP );

        assertThat( RoleRestriction.to( market, RoleKeys.ADMIN_LOGIN, EDITOR ).apply( admin ) )
            .as( "held memberships first, then the named roles not held" )
            .containsExactly( RoleKeys.ADMIN_LOGIN, market, EDITOR );
        assertThat( RoleRestriction.to( market ).apply( admin ) ).as( "system.admin is dropped unless named" ).containsExactly( market );
        assertThat( RoleRestriction.to( RoleKeys.ADMIN, market ).apply( admin ) ).containsExactly( RoleKeys.ADMIN, market );
        assertThat( RoleRestriction.to().apply( admin ) ).as( "to nothing is nothing for an administrator too" ).isEmpty();
        assertThat( RoleRestriction.to( market ).apply( MEMBERSHIPS ) ).as( "no one else gains a role" ).isEmpty();
    }

    @Test
    void to_nothing_keeps_nothing_and_is_not_none()
    {
        final RoleRestriction nothing = RoleRestriction.to();
        assertThat( nothing.isRestricted() ).isTrue();
        assertThat( nothing.apply( MEMBERSHIPS ) ).isEmpty();
        assertThat( nothing ).isEqualTo( RoleRestriction.to( PrincipalKeys.empty() ) ).isNotEqualTo( RoleRestriction.none() );
        assertThatThrownBy( () -> RoleRestriction.to( (PrincipalKeys) null ) ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void narrow_keeps_what_passes_both()
    {
        final RoleRestriction some = RoleRestriction.to( RoleKeys.ADMIN_LOGIN, EDITOR );
        final RoleRestriction other = RoleRestriction.to( EDITOR, RoleKeys.ADMIN );
        assertThat( RoleRestriction.none().narrow( RoleRestriction.none() ) ).isEqualTo( RoleRestriction.none() );
        assertThat( RoleRestriction.none().narrow( some ) ).isEqualTo( some );
        assertThat( some.narrow( RoleRestriction.none() ) ).isEqualTo( some );
        assertThat( some.narrow( other ) ).isEqualTo( RoleRestriction.to( EDITOR ) );
        assertThat( other.narrow( some ) ).isEqualTo( RoleRestriction.to( EDITOR ) );
        assertThat( some.narrow( RoleRestriction.to() ) ).isEqualTo( RoleRestriction.to() );
        assertThat( RoleRestriction.to().narrow( some ) ).isEqualTo( RoleRestriction.to() );
        assertThat( some.narrow( some ) ).isEqualTo( some );
        assertThat( some.narrow( RoleRestriction.to( RoleKeys.ADMIN ) ) ).as( "narrowing combines restrictions, not memberships" )
            .isEqualTo( RoleRestriction.to() );
    }
}
