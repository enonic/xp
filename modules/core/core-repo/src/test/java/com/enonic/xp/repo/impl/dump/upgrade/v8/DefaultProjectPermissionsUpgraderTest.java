package com.enonic.xp.repo.impl.dump.upgrade.v8;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultProjectPermissionsUpgraderTest
{
    private final DefaultProjectPermissionsUpgrader upgrader = new DefaultProjectPermissionsUpgrader();

    private static final RepositoryId DEFAULT_REPO = RepositoryId.from( "com.enonic.cms.default" );

    @Test
    void upgrades_permissions_in_default_project()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.permissions().contains( RoleKeys.CONTENT_MANAGER_APP ) ).isFalse();
        assertThat( result.permissions().contains( PrincipalKey.ofRole( "cms.project.default.owner" ) ) ).isTrue();
        assertThat( result.permissions().contains( PrincipalKey.ofRole( "cms.project.default.editor" ) ) ).isTrue();
        assertThat( result.permissions().contains( PrincipalKey.ofRole( "cms.project.default.author" ) ) ).isTrue();
        assertThat( result.permissions().contains( PrincipalKey.ofRole( "cms.project.default.contributor" ) ) ).isTrue();
        assertThat( result.permissions().contains( PrincipalKey.ofRole( "cms.project.default.viewer" ) ) ).isTrue();
    }

    @Test
    void owner_gets_all_permissions()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        final PrincipalKey owner = PrincipalKey.ofRole( "cms.project.default.owner" );
        assertThat( result.permissions()
                        .isAllowedFor( owner, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE,
                                       Permission.PUBLISH ) ).isTrue();
    }

    @Test
    void viewer_gets_read_only()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        final PrincipalKey viewer = PrincipalKey.ofRole( "cms.project.default.viewer" );
        assertThat( result.permissions().isAllowedFor( viewer, Permission.READ ) ).isTrue();
        assertThat( result.permissions().isAllowedFor( viewer, Permission.CREATE ) ).isFalse();
    }

    @Test
    void skips_non_default_project_repo()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( RepositoryId.from( "com.enonic.cms.other" ), nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_non_project_repo()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( RepositoryId.from( "system.repo" ), nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void skips_node_without_content_manager_app()
    {
        final NodeStoreVersion nodeVersion =
            createNodeVersion( AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNull();
    }

    @Test
    void issue_node_gets_issue_permissions()
    {
        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( NodeType.from( "issue" ) )
            .permissions( AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.permissions().contains( RoleKeys.CONTENT_MANAGER_APP ) ).isFalse();

        final PrincipalKey contributor = PrincipalKey.ofRole( "cms.project.default.contributor" );
        assertThat( result.permissions()
                        .isAllowedFor( contributor, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ) ).isTrue();
        assertThat( result.permissions().isAllowedFor( contributor, Permission.PUBLISH ) ).isFalse();
    }

    @Test
    void root_issue_node_gets_issue_permissions()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "title", "Root issue" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( NodeType.DEFAULT_NODE_COLLECTION )
            .data( data )
            .permissions( AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) )
            .build();

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();

        final PrincipalKey contributor = PrincipalKey.ofRole( "cms.project.default.contributor" );
        assertThat( result.permissions()
                        .isAllowedFor( contributor, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ) ).isTrue();
    }

    @Test
    void content_node_gets_content_permissions()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();

        final PrincipalKey contributor = PrincipalKey.ofRole( "cms.project.default.contributor" );
        assertThat( result.permissions().isAllowedFor( contributor, Permission.READ ) ).isTrue();
        assertThat( result.permissions().isAllowedFor( contributor, Permission.CREATE ) ).isFalse();
    }

    @Test
    void preserves_existing_permissions()
    {
        final NodeStoreVersion nodeVersion = createNodeVersion(
            AccessControlList.of( AccessControlEntry.create().allowAll().principal( RoleKeys.CONTENT_MANAGER_APP ).build(),
                                  AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ) );

        final NodeStoreVersion result = upgrader.upgradeNodeVersion( DEFAULT_REPO, nodeVersion );

        assertThat( result ).isNotNull();
        assertThat( result.permissions().contains( RoleKeys.ADMIN ) ).isTrue();
        assertThat( result.permissions().contains( RoleKeys.CONTENT_MANAGER_APP ) ).isFalse();
    }

    private static NodeStoreVersion createNodeVersion( final AccessControlList permissions )
    {
        return NodeStoreVersion.create()
            .id( NodeId.from( "test-node" ) )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION )
            .permissions( permissions )
            .build();
    }
}
