package com.enonic.xp.core.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.UpdateUserStoreParams;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.UserStoreAccess;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

import static com.enonic.wem.api.security.acl.Permission.CREATE;
import static com.enonic.wem.api.security.acl.Permission.DELETE;
import static com.enonic.wem.api.security.acl.Permission.MODIFY;
import static com.enonic.wem.api.security.acl.Permission.PUBLISH;
import static com.enonic.wem.api.security.acl.Permission.READ;
import static com.enonic.wem.api.security.acl.Permission.READ_PERMISSIONS;
import static com.enonic.wem.api.security.acl.Permission.WRITE_PERMISSIONS;
import static com.enonic.wem.api.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.wem.api.security.acl.UserStoreAccess.CREATE_USERS;
import static com.enonic.wem.api.security.acl.UserStoreAccess.USER_STORE_MANAGER;
import static com.enonic.wem.api.security.acl.UserStoreAccess.WRITE_USERS;

abstract class UserStoreNodeTranslator
{
    final static String USER_FOLDER_NODE_NAME = "users";

    final static String GROUP_FOLDER_NODE_NAME = "groups";

    static NodePath getRolesNodePath()
    {
        return NodePath.newNodePath( NodePath.ROOT, PrincipalKey.ROLES_NODE_NAME ).build();
    }

    static NodePath getUserStoresParentPath()
    {
        return NodePath.ROOT;
    }

    static UserStoreKey toKey( final Node node )
    {
        final String userStoreId = node.name().toString();
        return new UserStoreKey( userStoreId );
    }

    static NodePath toUserStoreNodePath( final UserStoreKey userStoreKey )
    {
        final NodePath userStoreParentPath = UserStoreNodeTranslator.getUserStoresParentPath();
        return new NodePath( userStoreParentPath, NodeName.from( userStoreKey.toString() ) );
    }

    static NodePath toUserStoreUsersNodePath( final UserStoreKey userStoreKey )
    {
        return new NodePath( toUserStoreNodePath( userStoreKey ), NodeName.from( USER_FOLDER_NODE_NAME ) );
    }

    static NodePath toUserStoreGroupsNodePath( final UserStoreKey userStoreKey )
    {
        return new NodePath( toUserStoreNodePath( userStoreKey ), NodeName.from( GROUP_FOLDER_NODE_NAME ) );
    }

    static UserStores fromNodes( final Nodes nodes )
    {
        final UserStore[] userStores = nodes.stream().
            map( UserStoreNodeTranslator::createUserStoreFromNode ).
            filter( Objects::nonNull ).
            toArray( UserStore[]::new );
        return UserStores.from( userStores );
    }

    static UserStore fromNode( final Node node )
    {
        return createUserStoreFromNode( node );
    }

    static UserStoreAccessControlList userStorePermissionsFromNode( final Node userStoreNode, final Node usersNode, final Node groupsNode )
    {
        final UserStoreAccessControlList.Builder acl = UserStoreAccessControlList.create();

        final AccessControlList userStorePermissions = userStoreNode.getPermissions();
        final AccessControlList usersPermissions = usersNode.getPermissions();
        final AccessControlList groupsPermissions = groupsNode.getPermissions();

        final PrincipalKeys principals = PrincipalKeys.from( userStorePermissions.getAllPrincipals(), usersPermissions.getAllPrincipals(),
                                                             groupsPermissions.getAllPrincipals() );
        for ( PrincipalKey principal : principals )
        {
            if ( userStorePermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS,
                                                    WRITE_PERMISSIONS ) &&
                usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ) &&
                groupsPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ) )
            {
                final UserStoreAccessControlEntry access = UserStoreAccessControlEntry.create().
                    principal( principal ).access( ADMINISTRATOR ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) &&
                groupsPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) )
            {
                final UserStoreAccessControlEntry access = UserStoreAccessControlEntry.create().
                    principal( principal ).access( USER_STORE_MANAGER ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) )
            {
                final UserStoreAccessControlEntry access = UserStoreAccessControlEntry.create().
                    principal( principal ).access( WRITE_USERS ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, CREATE ) )
            {
                final UserStoreAccessControlEntry access = UserStoreAccessControlEntry.create().
                    principal( principal ).access( CREATE_USERS ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ ) )
            {
                final UserStoreAccessControlEntry access = UserStoreAccessControlEntry.create().
                    principal( principal ).access( UserStoreAccess.READ ).build();
                acl.add( access );
            }
        }

        return acl.build();
    }

    static AccessControlList userStorePermissionsToUserStoreNodePermissions( final UserStoreAccessControlList userStorePermissions )
    {
        final List<AccessControlEntry> entries = new ArrayList<>();
        for ( UserStoreAccessControlEntry entry : userStorePermissions )
        {
            if ( entry.getAccess() == ADMINISTRATOR )
            {
                final AccessControlEntry ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                    allow( READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ).build();
                entries.add( ace );
            }
        }
        return AccessControlList.create().addAll( entries ).build();
    }

    static AccessControlList userStorePermissionsToUsersNodePermissions( final UserStoreAccessControlList userStorePermissions )
    {
        final List<AccessControlEntry> entries = new ArrayList<>();
        for ( UserStoreAccessControlEntry entry : userStorePermissions )
        {
            final AccessControlEntry ace;
            switch ( entry.getAccess() )
            {
                case CREATE_USERS:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( CREATE ).build();
                    entries.add( ace );
                    break;
                case WRITE_USERS:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ, CREATE, MODIFY, DELETE ).build();
                    entries.add( ace );
                    break;
                case USER_STORE_MANAGER:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ, CREATE, MODIFY, DELETE ).build();
                    entries.add( ace );
                    break;
                case ADMINISTRATOR:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ).build();
                    entries.add( ace );
                    break;
                case READ:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ ).build();
                    entries.add( ace );
                    break;
            }
        }
        return AccessControlList.create().addAll( entries ).build();
    }

    static AccessControlList userStorePermissionsToGroupsNodePermissions( final UserStoreAccessControlList userStorePermissions )
    {
        final List<AccessControlEntry> entries = new ArrayList<>();
        for ( UserStoreAccessControlEntry entry : userStorePermissions )
        {
            final AccessControlEntry ace;
            switch ( entry.getAccess() )
            {
                case USER_STORE_MANAGER:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ, CREATE, MODIFY, DELETE ).build();
                    entries.add( ace );
                    break;
                case ADMINISTRATOR:
                    ace = AccessControlEntry.create().principal( entry.getPrincipal() ).
                        allow( READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ).build();
                    entries.add( ace );
                    break;
            }
        }
        return AccessControlList.create().addAll( entries ).build();
    }

    static UpdateNodeParams toUpdateNodeParams( final UpdateUserStoreParams updateUserStoreParams, final NodeId nodeId )
    {
        return UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                nodeData.setString( UserStorePropertyNames.DISPLAY_NAME_KEY, updateUserStoreParams.getDisplayName() );
            } ).
            build();
    }

    private static UserStore createUserStoreFromNode( final Node node )
    {
        if ( node.name().toString().equalsIgnoreCase( PrincipalKey.ROLES_NODE_NAME ) )
        {
            return null;
        }
        final PropertySet nodeAsSet = node.data().getRoot();

        return UserStore.newUserStore().
            displayName( nodeAsSet.getString( UserStorePropertyNames.DISPLAY_NAME_KEY ) ).
            key( UserStoreNodeTranslator.toKey( node ) ).
            build();
    }

}
