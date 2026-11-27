package com.enonic.xp.security.token;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopeTest
{
    private static final Scope READ = Scope.create().id( "app:read" ).build();

    private static final Scope WRITE = Scope.create().id( "app:write" ).includes( List.of( "app:read" ) ).build();

    private static final Scope ALL = Scope.create().id( "app:all" ).includes( List.of( "app:write", "app:all" ) ).required( true ).build();

    private static final Scopes SCOPES = Scopes.from( READ, WRITE, ALL );

    @Test
    void defaults()
    {
        assertThat( READ.getTitle() ).as( "the title falls back to the id" ).isEqualTo( "app:read" );
        assertThat( READ.getDescription() ).isNull();
        assertThat( READ.getIncludes() ).isEmpty();
        assertThat( READ.getApplication() ).isNull();
        assertThat( READ.isRequired() ).isFalse();
        assertThat( ALL.isRequired() ).isTrue();
    }

    @Test
    void only_scope_names()
    {
        assertThatThrownBy( () -> Scope.create().id( "content read" ).build() ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "Not a scope name" );
        assertThatThrownBy( () -> Scope.create().id( "x" ).includes( List.of( "a b" ) ).build() ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> Scope.create().build() ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void a_scope_stands_for_those_it_includes_through_others_too()
    {
        assertThat( SCOPES.expand( Set.of( "app:read" ) ) ).containsExactly( "app:read" );
        assertThat( SCOPES.expand( Set.of( "app:write" ) ) ).containsExactlyInAnyOrder( "app:write", "app:read" );
        assertThat( SCOPES.expand( Set.of( "app:all" ) ) ).as( "a scope that names itself does not loop" )
            .containsExactlyInAnyOrder( "app:all", "app:write", "app:read" );
        assertThat( SCOPES.expand( Set.of( "openid", "app:read" ) ) ).as( "an unknown scope stands for itself" )
            .containsExactlyInAnyOrder( "openid", "app:read" );
        assertThat( SCOPES.expand( Set.of() ) ).isEmpty();
    }

    @Test
    void xp_scopes()
    {
        final Scopes xp = XpScopes.all();
        assertThat( xp.getIds() ).containsExactly( "admin", "read", "content", "read:content", "repo", "read:repo", "app", "read:app",
                                                   "idprovider", "read:idprovider", "system", "read:system" );
        assertThat( xp.expand( Set.of( "content" ) ) ).containsExactlyInAnyOrder( "content", "read:content" );
        assertThat( xp.expand( Set.of( "read" ) ) ).containsExactlyInAnyOrder( "read", "read:content", "read:repo", "read:app",
                                                                               "read:idprovider", "read:system" );
        assertThat( xp.expand( Set.of( "admin" ) ) ).containsExactlyInAnyOrder( xp.getIds().toArray( String[]::new ) );
        assertThat( xp.expand( Set.of( "read:repo" ) ) ).containsExactly( "read:repo" );
    }
}
