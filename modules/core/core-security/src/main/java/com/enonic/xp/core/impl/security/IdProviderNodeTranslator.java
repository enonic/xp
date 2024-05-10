package com.enonic.xp.core.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.IdProviderAccess;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

import static com.enonic.xp.security.acl.IdProviderAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.IdProviderAccess.CREATE_USERS;
import static com.enonic.xp.security.acl.IdProviderAccess.ID_PROVIDER_MANAGER;
import static com.enonic.xp.security.acl.IdProviderAccess.WRITE_USERS;
import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static com.enonic.xp.security.acl.Permission.READ_PERMISSIONS;
import static com.enonic.xp.security.acl.Permission.WRITE_PERMISSIONS;

abstract class IdProviderNodeTranslator
{
    static final String USER_FOLDER_NODE_NAME = "users";

    static final String GROUP_FOLDER_NODE_NAME = "groups";

    static final ApplicationKey SYSTEM_ID_PROVIDER_KEY = ApplicationKey.from( "com.enonic.xp.app.standardidprovider" );

    static final NodePath ID_PROVIDERS_PARENT_PATH = new NodePath( NodePath.ROOT, NodeName.from( PrincipalKey.IDENTITY_NODE_NAME ) );

    static IdProviderKey toKey( final Node node )
    {
        final String idProviderId = node.name().toString();
        return IdProviderKey.from( idProviderId );
    }

    static NodePath toIdProviderNodePath( final IdProviderKey idProviderKey )
    {
        return new NodePath( ID_PROVIDERS_PARENT_PATH, NodeName.from( idProviderKey.toString() ) );
    }

    static NodePath toIdProviderUsersNodePath( final IdProviderKey idProviderKey )
    {
        return new NodePath( toIdProviderNodePath( idProviderKey ), NodeName.from( USER_FOLDER_NODE_NAME ) );
    }

    static NodePath toIdProviderGroupsNodePath( final IdProviderKey idProviderKey )
    {
        return new NodePath( toIdProviderNodePath( idProviderKey ), NodeName.from( GROUP_FOLDER_NODE_NAME ) );
    }

    static IdProviders fromNodes( final Nodes nodes )
    {
        final IdProvider[] idProviders = nodes.stream().
            map( IdProviderNodeTranslator::createIdProviderFromNode ).
            filter( Objects::nonNull ).
            toArray( IdProvider[]::new );
        return IdProviders.from( idProviders );
    }

    static IdProvider fromNode( final Node node )
    {
        return createIdProviderFromNode( node );
    }

    static IdProviderAccessControlList idProviderPermissionsFromNode( final Node idProviderNode, final Node usersNode,
                                                                      final Node groupsNode )
    {
        final IdProviderAccessControlList.Builder acl = IdProviderAccessControlList.create();

        final AccessControlList idProviderPermissions = idProviderNode.getPermissions();
        final AccessControlList usersPermissions = usersNode.getPermissions();
        final AccessControlList groupsPermissions = groupsNode.getPermissions();

        final PrincipalKeys principals = PrincipalKeys.from( idProviderPermissions.getAllPrincipals(), usersPermissions.getAllPrincipals(),
                                                             groupsPermissions.getAllPrincipals() );
        for ( PrincipalKey principal : principals )
        {
            if ( idProviderPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS,
                                                     WRITE_PERMISSIONS ) &&
                usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ) &&
                groupsPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS ) )
            {
                final IdProviderAccessControlEntry access = IdProviderAccessControlEntry.create().
                    principal( principal ).access( ADMINISTRATOR ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) &&
                groupsPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) )
            {
                final IdProviderAccessControlEntry access = IdProviderAccessControlEntry.create().
                    principal( principal ).access( ID_PROVIDER_MANAGER ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ, CREATE, MODIFY, DELETE ) )
            {
                final IdProviderAccessControlEntry access = IdProviderAccessControlEntry.create().
                    principal( principal ).access( WRITE_USERS ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, CREATE ) )
            {
                final IdProviderAccessControlEntry access = IdProviderAccessControlEntry.create().
                    principal( principal ).access( CREATE_USERS ).build();
                acl.add( access );
            }
            else if ( usersPermissions.isAllowedFor( principal, READ ) )
            {
                final IdProviderAccessControlEntry access = IdProviderAccessControlEntry.create().
                    principal( principal ).access( IdProviderAccess.READ ).build();
                acl.add( access );
            }
        }

        return acl.build();
    }

    static AccessControlList idProviderPermissionsToIdProviderNodePermissions( final IdProviderAccessControlList idProviderPermissions )
    {
        final List<AccessControlEntry> entries = new ArrayList<>();
        for ( IdProviderAccessControlEntry entry : idProviderPermissions )
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

    static AccessControlList idProviderPermissionsToUsersNodePermissions( final IdProviderAccessControlList idProviderPermissions )
    {
        final List<AccessControlEntry> entries = new ArrayList<>();
        for ( IdProviderAccessControlEntry entry : idProviderPermissions )
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
                case ID_PROVIDER_MANAGER:
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

    static AccessControlList idProviderPermissionsToGroupsNodePermissions( final IdProviderAccessControlList idProviderPermissions )
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( IdProviderAccessControlEntry entry : idProviderPermissions )
        {
            switch ( entry.getAccess() )
            {
                case ID_PROVIDER_MANAGER:
                    builder.add(
                        AccessControlEntry.create().principal( entry.getPrincipal() ).allow( READ, CREATE, MODIFY, DELETE ).build() );
                    break;
                case ADMINISTRATOR:
                    builder.add( AccessControlEntry.create()
                                     .principal( entry.getPrincipal() )
                                     .allow( READ, CREATE, MODIFY, DELETE, PUBLISH, READ_PERMISSIONS, WRITE_PERMISSIONS )
                                     .build() );
                    break;
                default:
                    break;
            }
        }
        return builder.build();
    }

    static UpdateNodeParams toUpdateNodeParams( final IdProvider idProvider, final NodeId nodeId )
    {
        return UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                nodeData.setString( IdProviderPropertyNames.DISPLAY_NAME_KEY, idProvider.getDisplayName() );
                nodeData.setString( IdProviderPropertyNames.DESCRIPTION_KEY, idProvider.getDescription() );

                final IdProviderConfig idProviderConfig = idProvider.getIdProviderConfig();
                if ( idProviderConfig == null )
                {
                    nodeData.removeProperties( IdProviderPropertyNames.APPLICATION );
                }
                else
                {
                    nodeData.setString( IdProviderPropertyNames.ID_PROVIDER_APPLICATION_KEY,
                                        idProviderConfig.getApplicationKey().toString() );
                    nodeData.setSet( IdProviderPropertyNames.ID_PROVIDER_CONFIG_FORM_KEY,
                                     idProviderConfig.getConfig().getRoot().copy( nodeData ) );
                }
            } ).
            refresh( RefreshMode.ALL ).
            build();
    }

    private static IdProvider createIdProviderFromNode( final Node node )
    {
        if ( node.name().toString().equalsIgnoreCase( PrincipalKey.ROLES_NODE_NAME ) )
        {
            return null;
        }
        final PropertySet nodeAsSet = node.data().getRoot();

        final IdProviderKey idProviderKey = IdProviderNodeTranslator.toKey( node );

        final IdProvider.Builder idProvider = IdProvider.create().
            displayName( nodeAsSet.getString( IdProviderPropertyNames.DISPLAY_NAME_KEY ) ).
            key( idProviderKey ).
            description( nodeAsSet.getString( IdProviderPropertyNames.DESCRIPTION_KEY ) );

        if ( nodeAsSet.hasProperty( IdProviderPropertyNames.APPLICATION ) )
        {
            final String applicationKey = nodeAsSet.getString( IdProviderPropertyNames.ID_PROVIDER_APPLICATION_KEY );
            final PropertySet config = nodeAsSet.getSet( IdProviderPropertyNames.ID_PROVIDER_CONFIG_FORM_KEY );
            final IdProviderConfig idProviderConfig = IdProviderConfig.create().
                applicationKey( ApplicationKey.from( applicationKey ) ).
                config( config.toTree() ).
                build();
            idProvider.idProviderConfig( idProviderConfig );
        }
        else if ( IdProviderKey.system().equals( idProviderKey ) )
        {
            //TODO Remove after next dump upgrade
            final IdProviderConfig idProviderConfig = IdProviderConfig.create().
                applicationKey( SYSTEM_ID_PROVIDER_KEY ).
                build();
            idProvider.idProviderConfig( idProviderConfig );
        }

        return idProvider.build();
    }

}
