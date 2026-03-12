package com.enonic.xp.repo.impl.dump.upgrade.v8;

import java.util.List;
import java.util.Set;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.security.SystemConstants;

class DefaultProjectRolesCreator
{
    private static final String PROJECT_NAME = "default";

    private static final String DISPLAY_NAME = "Default";

    private static final List<RoleDefinition> ROLES = List.of(
        new RoleDefinition( "owner", "Owner" ),
        new RoleDefinition( "editor", "Editor" ),
        new RoleDefinition( "author", "Author" ),
        new RoleDefinition( "contributor", "Contributor" ),
        new RoleDefinition( "viewer", "Viewer" ) );

    List<NewDumpNode> createRoleNodes()
    {
        return ROLES.stream().map( this::createRoleNode ).toList();
    }

    private NewDumpNode createRoleNode( final RoleDefinition role )
    {
        final String roleId = ProjectConstants.PROJECT_NAME_PREFIX + PROJECT_NAME + "." + role.id;
        final String nodeId = "role:" + roleId;
        final String nodePath = "/identity/roles/" + roleId;

        final PropertyTree data = new PropertyTree();
        data.setString( "displayName", DISPLAY_NAME + " - " + role.displayName );
        data.setString( "principalType", "ROLE" );

        final NodeStoreVersion nodeVersion = NodeStoreVersion.create()
            .id( NodeId.from( nodeId ) )
            .childOrder( ChildOrder.defaultOrder() )
            .data( data )
            .build();

        return new NewDumpNode( nodeId, nodePath, nodeVersion, Set.of( SystemConstants.BRANCH_SYSTEM ) );
    }

    private record RoleDefinition(String id, String displayName)
    {
    }
}
