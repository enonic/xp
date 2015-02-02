package com.enonic.wem.core.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.User;

import static com.enonic.wem.core.security.PrincipalPropertyNames.AUTHENTICATION_HASH_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.DISPLAY_NAME_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.EMAIL_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.LOGIN_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.MEMBER_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.PRINCIPAL_TYPE_KEY;
import static com.enonic.wem.core.security.PrincipalPropertyNames.USER_STORE_KEY;

abstract class PrincipalNodeTranslator
{
    static Principals fromNodes( final Nodes nodes )
    {
        final LinkedHashSet<Principal> principals = Sets.newLinkedHashSet();

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
        if ( nodeAsSet.isNull( PRINCIPAL_TYPE_KEY ) )
        {
            throw new IllegalArgumentException( "Property " + PRINCIPAL_TYPE_KEY + " not found on node with id " + node.id() );
        }

        final PrincipalType principalType = PrincipalType.valueOf( nodeAsSet.getString( PRINCIPAL_TYPE_KEY ) );

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
            setNodeId( NodeId.from( principal.getKey() ) ).
            inheritPermissions( true ).
            indexConfigDocument( PrincipalIndexConfigFactory.create() );

        final PropertyTree data = new PropertyTree();
        data.setString( DISPLAY_NAME_KEY, principal.getDisplayName() );
        data.setString( PRINCIPAL_TYPE_KEY, principal.getKey().getType().toString() );
        if ( !principal.getKey().isRole() )
        {
            data.setString( USER_STORE_KEY, principal.getKey().getUserStore().toString() );
        }

        switch ( principal.getKey().getType() )
        {
            case USER:
                populateUserData( data.getRoot(), (User) principal );
                break;
        }

        builder.data( data );

        return builder.build();
    }

    public static UpdateNodeParams toUpdateNodeParams( final Principal principal )
    {
        Preconditions.checkNotNull( principal );

        return UpdateNodeParams.create().
            id( NodeId.from( principal.getKey() ) ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                nodeData.setString( DISPLAY_NAME_KEY, principal.getDisplayName() );
                switch ( principal.getKey().getType() )
                {
                    case USER:
                        populateUserData( nodeData.getRoot(), (User) principal );
                }
            } ).
            build();
    }

    static UpdateNodeParams addRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        return UpdateNodeParams.create().
            id( NodeId.from( relationship.getFrom() ) ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                final String relationshipToKey = relationship.getTo().toString();
                final boolean relationshipInList = nodeData.getProperties( MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    map( Value::asString ).
                    anyMatch( relationshipToKey::equals );

                if ( !relationshipInList )
                {
                    nodeData.addString( MEMBER_KEY, relationshipToKey );
                }
            } ).
            build();
    }

    static UpdateNodeParams removeRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        return UpdateNodeParams.create().
            id( NodeId.from( relationship.getFrom() ) ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                final String relationshipToKey = relationship.getTo().toString();

                final List<Value> updatedMembers = nodeData.getProperties( MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    filter( ( val ) -> !relationshipToKey.equals( val.asString() ) ).
                    collect( Collectors.toList() );

                nodeData.removeProperty( MEMBER_KEY );
                nodeData.setValues( MEMBER_KEY, updatedMembers );
            } ).
            build();
    }

    static UpdateNodeParams removeAllRelationshipsToUpdateNodeParams( final PrincipalKey from )
    {
        Preconditions.checkNotNull( from );

        return UpdateNodeParams.create().
            id( NodeId.from( from ) ).
            editor( editableNode -> {
                final PropertyTree data = editableNode.data;
                data.removeProperties( MEMBER_KEY );
            } ).
            build();
    }

    static PrincipalRelationships relationshipsFromNode( final Node node )
    {
        final PropertyTree rootDataSet = node.data();
        final List<Property> members = rootDataSet.getProperties( MEMBER_KEY );
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
        data.setString( EMAIL_KEY, user.getEmail() );
        data.setString( LOGIN_KEY, user.getLogin() );
        data.setString( AUTHENTICATION_HASH_KEY, user.getAuthenticationHash() );
    }

    private static User createUserFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        return User.create().
            email( nodeAsTree.getString( EMAIL_KEY ) ).
            login( nodeAsTree.getString( LOGIN_KEY ) ).
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( DISPLAY_NAME_KEY ) ).
            authenticationHash( nodeAsTree.getString( AUTHENTICATION_HASH_KEY ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }

    private static Group createGroupFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        return Group.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( DISPLAY_NAME_KEY ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }

    private static Role createRoleFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final PropertyTree nodeAsTree = node.data();

        return Role.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( nodeAsTree.getString( DISPLAY_NAME_KEY ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }


}
