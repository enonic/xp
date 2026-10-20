package com.enonic.xp.web.vhost;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualHostIdProviderTest
{
    private static final VirtualHostIdProvider CATALOGUE = VirtualHostIdProvider.create()
        .scopes( Map.of( "content:read", VirtualHostIdProvider.Scope.of( PrincipalKeys.from( "role:cms.project.site.viewer" ) ), "content:write",
                         VirtualHostIdProvider.Scope.of( PrincipalKeys.from( "role:cms.project.site.viewer", "role:cms.project.site.editor" ) ),
                         "profile", new VirtualHostIdProvider.Scope( PrincipalKeys.empty(), true ) ) )
        .build();

    @Test
    void no_catalogue_implies_no_restriction()
    {
        assertEquals( RoleRestriction.none(), VirtualHostIdProvider.create().build().restrictionFor( List.of( "content:read" ) ) );
        assertEquals( RoleRestriction.none(), VirtualHostIdProvider.create().build().restrictionFor( List.of() ) );
    }

    @Test
    void scopes_imply_the_union_of_their_roles()
    {
        assertEquals( RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer", "role:cms.project.site.editor" ) ),
                      CATALOGUE.restrictionFor( List.of( "content:read", "content:write" ) ) );
        assertEquals( RoleRestriction.to( PrincipalKeys.from( "role:cms.project.site.viewer" ) ), CATALOGUE.restrictionFor( List.of( "content:read" ) ) );
    }

    @Test
    void unknown_and_label_only_scopes_imply_nothing()
    {
        assertEquals( RoleRestriction.to(), CATALOGUE.restrictionFor( List.of( "profile", "unknown" ) ) );
        assertEquals( RoleRestriction.to(), CATALOGUE.restrictionFor( Set.of() ) );
    }
}
