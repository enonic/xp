package com.enonic.xp.security.token;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopeTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final Scope READ =
        Scope.create().idProvider( IDP ).id( "content:read" ).roles( PrincipalKeys.from( "role:cms.project.site.viewer" ) ).build();

    private static final Scope WRITE = Scope.create()
        .idProvider( IDP )
        .id( "content:write" )
        .roles( PrincipalKeys.from( "role:cms.project.site.viewer", "role:cms.project.site.editor" ) )
        .build();

    private static final Scope PROFILE = Scope.create().idProvider( IDP ).id( "profile" ).required( true ).build();

    private static final Scopes SCOPES = Scopes.from( READ, WRITE, PROFILE );

    @Test
    void defaults()
    {
        assertThat( PROFILE.getTitle() ).as( "the title falls back to the id" ).isEqualTo( "profile" );
        assertThat( PROFILE.getDescription() ).isNull();
        assertThat( PROFILE.getRoles() ).isEmpty();
        assertThat( PROFILE.isRequired() ).isTrue();
        assertThat( READ.isRequired() ).isFalse();
    }

    @Test
    void only_roles_and_only_scope_names()
    {
        assertThatThrownBy( () -> Scope.create().idProvider( IDP ).id( "x" ).roles( PrincipalKeys.from( "user:system:su" ) ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "only imply roles" );
        assertThatThrownBy( () -> Scope.create().idProvider( IDP ).id( "content read" ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "Not a scope name" );
        assertThatThrownBy( () -> Scope.create().id( "x" ).build() ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void no_scopes_imply_no_restriction()
    {
        assertThat( Scopes.empty().restrictionFor( List.of( "content:read" ) ) ).isEqualTo( RoleRestriction.none() );
        assertThat( Scopes.empty().restrictionFor( List.of() ) ).isEqualTo( RoleRestriction.none() );
    }

    @Test
    void approved_scopes_imply_the_union_of_their_roles()
    {
        assertThat( SCOPES.restrictionFor( List.of( "content:read", "content:write" ) ) ).isEqualTo(
            RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer", "role:cms.project.site.editor" ) ) );
        assertThat( SCOPES.restrictionFor( List.of( "content:read" ) ) ).isEqualTo(
            RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer" ) ) );
    }

    @Test
    void unknown_and_label_scopes_imply_nothing()
    {
        assertThat( SCOPES.restrictionFor( List.of( "profile", "unknown" ) ) ).isEqualTo( RoleRestriction.to() );
    }

    @Test
    void no_scope_approved_implies_no_restriction()
    {
        assertThat( SCOPES.restrictionFor( Set.of() ) ).isEqualTo( RoleRestriction.none() );
    }

    @Test
    void update_keeps_what_is_unset_and_clears_what_is_null()
    {
        final Scope described = Scope.create().idProvider( IDP ).id( "x" ).title( "X" ).description( "Does x" ).required( true ).build();

        assertThat( UpdateScopeParams.create().idProvider( IDP ).id( "x" ).editor( e -> {
        } ).build().applyTo( described ) ).isEqualTo( described );
        final Scope cleared = UpdateScopeParams.create().idProvider( IDP ).id( "x" ).editor( e -> {
            e.title = null;
            e.description = null;
        } ).build().applyTo( described );
        assertThat( cleared.getTitle() ).isEqualTo( "x" );
        assertThat( cleared.getDescription() ).isNull();
        assertThat( cleared.isRequired() ).isTrue();
        final Scope changed = UpdateScopeParams.create()
            .idProvider( IDP )
            .id( "x" )
            .editor( e -> {
                e.roles = PrincipalKeys.from( "role:a" );
                e.required = false;
            } )
            .build()
            .applyTo( described );
        assertThat( changed.getRoles() ).isEqualTo( PrincipalKeys.from( "role:a" ) );
        assertThat( changed.isRequired() ).isFalse();
        assertThat( changed.getTitle() ).isEqualTo( "X" );
    }
}
