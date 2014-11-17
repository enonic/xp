package com.enonic.wem.core.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.DataId;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.UpdateNodeParams;

abstract class PrincipalNodeTranslator
{
    public static final String DISPLAY_NAME_KEY = "displayName";

    public static final String PRINCIPAL_TYPE_KEY = "principalType";

    public static final String PRINCIPAL_KEY = "principalKey";

    public static final String USER_STORE_KEY = "userStoreKey";

    public static final String EMAIL_KEY = "email";

    public static final String LOGIN_KEY = "login";

    public static final String MEMBER_KEY = "member";

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
        final Property typeProperty = node.data().getProperty( PRINCIPAL_TYPE_KEY );

        if ( typeProperty == null )
        {
            throw new IllegalArgumentException( "Property " + PRINCIPAL_TYPE_KEY + " not found on node with id " + node.id() );
        }

        final PrincipalType principalType = PrincipalType.valueOf( typeProperty.getValue().asString() );

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

    private static String getDisplayNameProperty( final RootDataSet rootDataSet )
    {
        return getStringValue( rootDataSet, DISPLAY_NAME_KEY );
    }

    private static String getStringValue( final RootDataSet rootDataSet, final String key )
    {
        if ( rootDataSet.getProperty( key ) == null )
        {
            throw new IllegalArgumentException( "Required property " + key + " not found on Node" );
        }

        return rootDataSet.getProperty( key ).getValue().asString();
    }

    public static CreateNodeParams toCreateNodeParams( final Principal principal )
    {
        Preconditions.checkNotNull( principal );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( PrincipalKeyNodeTranslator.toNodeName( principal.getKey() ).toString() ).
            parent( PrincipalPathTranslator.toParentPath( principal.getKey() ) ).
            setNodeId( NodeId.from( principal.getKey() ) ).
            indexConfigDocument( PrincipalIndexConfigFactory.create() );

        final RootDataSet data = new RootDataSet();
        data.setProperty( DISPLAY_NAME_KEY, Value.newString( principal.getDisplayName() ) );
        data.setProperty( PRINCIPAL_TYPE_KEY, Value.newString( principal.getKey().getType() ) );
        data.setProperty( USER_STORE_KEY, Value.newString( principal.getKey().getUserStore().toString() ) );

        switch ( principal.getKey().getType() )
        {
            case USER:
                populateUserData( data, (User) principal );
                break;
        }

        builder.data( data );

        return builder.build();
    }

    public static UpdateNodeParams toUpdateNodeParams( final Principal principal )
    {
        Preconditions.checkNotNull( principal );

        final UpdateNodeParams updateNodeParams = new UpdateNodeParams().id( NodeId.from( principal.getKey() ) ).
            editor( toBeEdited -> {
                final RootDataSet data = toBeEdited.data().copy().toRootDataSet();

                data.setProperty( DISPLAY_NAME_KEY, Value.newString( principal.getDisplayName() ) );
                switch ( principal.getKey().getType() )
                {
                    case USER:
                        populateUserData( data, (User) principal );
                }

                return Node.editNode( toBeEdited ).rootDataSet( data );
            } );

        return updateNodeParams;
    }

    static UpdateNodeParams addRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        final UpdateNodeParams updateNodeParams = new UpdateNodeParams().id( NodeId.from( relationship.getFrom() ) ).
            editor( toBeEdited -> {
                final RootDataSet data = toBeEdited.data().copy().toRootDataSet();

                final String relationshipToKey = relationship.getTo().toString();

                final boolean relationshipInList = data.getPropertiesByName( MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    map( Value::asString ).
                    anyMatch( relationshipToKey::equals );

                if ( !relationshipInList )
                {
                    data.add( Property.newString( MEMBER_KEY, relationshipToKey ) );
                }

                return Node.editNode( toBeEdited ).rootDataSet( data );
            } );

        return updateNodeParams;
    }

    static UpdateNodeParams removeRelationshipToUpdateNodeParams( final PrincipalRelationship relationship )
    {
        Preconditions.checkNotNull( relationship );

        final UpdateNodeParams updateNodeParams = new UpdateNodeParams().id( NodeId.from( relationship.getFrom() ) ).
            editor( toBeEdited -> {
                final RootDataSet data = toBeEdited.data().copy().toRootDataSet();

                final String relationshipToKey = relationship.getTo().toString();

                final List<Value> updatedMembers = data.getPropertiesByName( MEMBER_KEY ).stream().
                    map( Property::getValue ).
                    filter( ( val ) -> !relationshipToKey.equals( val.asString() ) ).
                    collect( Collectors.toList() );
                final Value[] values = updatedMembers.toArray( new Value[updatedMembers.size()] );

                data.remove( DataId.from( MEMBER_KEY ) );
                data.setProperty( MEMBER_KEY, values );

                return Node.editNode( toBeEdited ).rootDataSet( data );
            } );

        return updateNodeParams;
    }

    static UpdateNodeParams removeAllRelationshipsToUpdateNodeParams( final PrincipalKey from )
    {
        Preconditions.checkNotNull( from );

        final UpdateNodeParams updateNodeParams = new UpdateNodeParams().id( NodeId.from( from ) ).
            editor( toBeEdited -> {
                final RootDataSet data = toBeEdited.data().copy().toRootDataSet();

                data.remove( DataId.from( MEMBER_KEY ) );

                return Node.editNode( toBeEdited ).rootDataSet( data );
            } );

        return updateNodeParams;
    }

    static PrincipalRelationships relationshipsFromNode( final Node node )
    {
        final RootDataSet rootDataSet = node.data();
        final List<Property> members = rootDataSet.getPropertiesByName( MEMBER_KEY );
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

    private static void populateUserData( final RootDataSet data, final User user )
    {
        data.setProperty( EMAIL_KEY, Value.newString( user.getEmail() ) );
        data.setProperty( LOGIN_KEY, Value.newString( user.getLogin() ) );
    }

    private static User createUserFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final RootDataSet rootDataSet = node.data();

        return User.create().
            email( getStringValue( rootDataSet, EMAIL_KEY ) ).
            login( getStringValue( rootDataSet, LOGIN_KEY ) ).
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( getDisplayNameProperty( node.data() ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }

    private static Group createGroupFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        return Group.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( getDisplayNameProperty( node.data() ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }

    private static Role createRoleFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        return Role.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( getDisplayNameProperty( node.data() ) ).
            modifiedTime( node.getModifiedTime() ).
            build();
    }


}
