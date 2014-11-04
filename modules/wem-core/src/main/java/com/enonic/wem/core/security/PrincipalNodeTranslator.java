package com.enonic.wem.core.security;

import java.util.LinkedHashSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.User;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.Nodes;

abstract class PrincipalNodeTranslator
{
    public static final String DISPLAY_NAME_KEY = "displayName";

    public static final String PRINCIPAL_TYPE_KEY = "principalType";

    public static final String PRINCIPAL_KEY = "principalKey";

    public static final String USERSTORE_KEY = "userStoreKey";

    public static final String EMAIL_KEY = "email";

    public static final String LOGIN_KEY = "login";

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
        data.setProperty( USERSTORE_KEY, Value.newString( principal.getKey().getUserStore().toString() ) );

        switch ( principal.getKey().getType() )
        {
            case USER:
                populateUserData( data, (User) principal );
        }

        builder.data( data );

        return builder.build();
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
            build();
    }

    private static Group createGroupFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        return Group.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( getDisplayNameProperty( node.data() ) ).
            build();
    }

    private static Role createRoleFromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        return Role.create().
            key( PrincipalKeyNodeTranslator.toKey( node ) ).
            displayName( getDisplayNameProperty( node.data() ) ).
            build();
    }


}
