package com.enonic.xp.core.impl.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;

abstract class PrincipalNodeTranslator
{
    static Principals fromNodes( final Nodes nodes )
    {
        final LinkedHashSet<Principal> principals = new LinkedHashSet<>();

        for ( final Node node : nodes )
        {
            principals.add( doCreatePrincipalFromNode( node ) );
        }

        return Principals.from( principals );
    }

    static Principal fromNode( final Node node )
    {
        return doCreatePrincipalFromNode( node );
    }

    static User userFromNode( final Node node )
    {
        return createUserFromNode( node );
    }

    static Group groupFromNode( final Node node )
    {
        return createGroupFromNode( node );
    }

    static Role roleFromNode( final Node node )
    {
        return createRoleFromNode( node );
    }

    private static Principal doCreatePrincipalFromNode( final Node node )
    {
        final PropertySet nodeAsSet = node.data().getRoot();
        if ( nodeAsSet.isNull( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) )
        {
            throw new IllegalArgumentException(
                "Property " + PrincipalPropertyNames.PRINCIPAL_TYPE_KEY + " not found on node with id " + node.id() );
        }

        final PrincipalType principalType = PrincipalType.valueOf( nodeAsSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) );

        switch ( principalType )
        {
            case USER:
                return createUserFromNode( node );
            case GROUP:
                return createGroupFromNode( node );
            case ROLE:
                return createRoleFromNode( node );
            default:
                throw new IllegalArgumentException( "Not able to translate principal-type " + principalType );
        }
    }

    public static CreateNodeParams toCreateNodeParams( final Principal principal )
    {
        Preconditions.checkNotNull( principal );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( PrincipalKeyNodeTranslator.toNodeName( principal.getKey() ).toString() ).
            parent( principal.getKey().toPath().getParentPath() ).
            setNodeId( NodeId.from( principal.getKey().toString().toLowerCase() ) ).
            inheritPermissions( true ).
            indexConfigDocument( PrincipalIndexConfigFactory.create() );

        final PropertyTree data = new PropertyTree();
        data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, principal.getDisplayName() );
        data.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, principal.getKey().getType().toString() );
        if ( !principal.getKey().isRole() )
        {
            data.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, principal.getKey().getIdProviderKey().toString() );
        }

        switch ( principal.getKey().getType() )
        {
            case USER:
                populateUserData( data.getRoot(), (User) principal );
                break;
            case ROLE:
                populateRoleData( data.getRoot(), (Role) principal );
                break;
            case GROUP:
                populateGroupData( data.getRoot(), (Group) principal );
                break;
        }

        builder.data( data );
        builder.refresh( RefreshMode.ALL );

        return builder.build();
    }

    public static UpdateNodeParams toUpdateNodeParams( final Principal principal )
    {
        Preconditions.checkNotNull( principal );

        return UpdateNodeParams.create().
            path( principal.getKey().toPath() ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                nodeData.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, principal.getDisplayName() );
                switch ( principal.getKey().getType() )
                {
                    case USER:
                        populateUserData( nodeData.getRoot(), (User) principal );
                        break;
                    case ROLE:
                        populateRoleData( nodeData.getRoot(), (Role) principal );
                        break;
                    case GROUP:
                        populateGroupData( nodeData.getRoot(), (Group) principal );
                        break;
                }
            } ).
            refresh( RefreshMode.ALL ).
            build();
    }

    static UpdateNodeParams addRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        return UpdateNodeParams.create().
            path( relationship.getFrom().toPath() ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                final String relationshipToKey = relationship.getTo().toString();
                final boolean relationshipInList = nodeData.getProperties( PrincipalPropertyNames.MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    map( Value::asString ).
                    anyMatch( relationshipToKey::equals );

                if ( !relationshipInList )
                {
                    nodeData.addString( PrincipalPropertyNames.MEMBER_KEY, relationshipToKey );
                }
            } ).
            refresh( RefreshMode.ALL ).
            build();
    }

    static UpdateNodeParams removeRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        return UpdateNodeParams.create().
            path( relationship.getFrom().toPath() ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                final String relationshipToKey = relationship.getTo().toString();

                final List<Value> updatedMembers = nodeData.getProperties( PrincipalPropertyNames.MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    filter( ( val ) -> !relationshipToKey.equals( val.asString() ) ).
                    collect( Collectors.toList() );

                nodeData.setValues( PrincipalPropertyNames.MEMBER_KEY, updatedMembers );
            } ).
            refresh( RefreshMode.ALL ).
            build();
    }

    static UpdateNodeParams removeAllRelationshipsToUpdateNodeParams( final PrincipalKey from )
    {
        Preconditions.checkNotNull( from );

        return UpdateNodeParams.create().
            path( from.toPath() ).
            editor( editableNode -> {
                final PropertyTree data = editableNode.data;
                data.removeProperties( PrincipalPropertyNames.MEMBER_KEY );
            } ).
            refresh( RefreshMode.ALL ).
            build();
    }

    static PrincipalRelationships relationshipsFromNode( final Node node )
    {
        final PropertyTree rootDataSet = node.data();
        final List<Property> members = rootDataSet.getProperties( PrincipalPropertyNames.MEMBER_KEY );
        if ( members == null || members.isEmpty() )
        {
            return PrincipalRelationships.empty();
        }

        final ImmutableList.Builder<PrincipalRelationship> relationships = ImmutableList.builder();
        final PrincipalKey relationshipFrom = PrincipalKeyNodeTranslator.toKey( node );
        for ( Property member : members )
        {
            final String memberKey = member.getValue().asString();
            final PrincipalKey relationshipTo = PrincipalKey.from( memberKey );
            final PrincipalRelationship relationship = PrincipalRelationship.from( relationshipFrom ).to( relationshipTo );
            relationships.add( relationship );
        }
        return PrincipalRelationships.from( relationships.build() );
    }

    private static void populateUserData( final PropertySet data, final User user )
    {
        data.setString( PrincipalPropertyNames.EMAIL_KEY, user.getEmail() );
        data.setString( PrincipalPropertyNames.LOGIN_KEY, user.getLogin() );
        data.setString( PrincipalPropertyNames.AUTHENTICATION_HASH_KEY, user.getAuthenticationHash() );
        data.setSet( PrincipalPropertyNames.PROFILE_KEY, user.getProfile().getRoot().copy( data.getTree() ) );
    }

    private static void populateRoleData( final PropertySet data, final Role role )
    {
        data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, role.getDescription() );
    }

    private static void populateGroupData( final PropertySet data, final Group group )
    {
        data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, group.getDescription() );
    }

    private static User createUserFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        final User.Builder user = User.create().
            email( nodeAsTree.getString( PrincipalPropertyNames.EMAIL_KEY ) ).
            login( nodeAsTree.getString( PrincipalPropertyNames.LOGIN_KEY ) ).
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) ).
            authenticationHash( nodeAsTree.getString( PrincipalPropertyNames.AUTHENTICATION_HASH_KEY ) );

        createUserProfileFromNode( nodeAsTree, user );

        return user.build();
    }

    private static void createUserProfileFromNode( final PropertyTree nodeAsTree, final User.Builder user )
    {
        final PropertySet nodeProfile = nodeAsTree.getSet( PrincipalPropertyNames.PROFILE_KEY );
        final PropertyTree profile = nodeProfile == null ? new PropertyTree() : nodeProfile.toTree();
        user.profile( profile );
    }

    private static Group createGroupFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        return Group.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) ).
            description( nodeAsTree.getString( PrincipalPropertyNames.DESCRIPTION_KEY ) ).
            build();
    }

    private static Role createRoleFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        return Role.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) ).
            description( nodeAsTree.getString( PrincipalPropertyNames.DESCRIPTION_KEY ) ).
            build();
    }


}
