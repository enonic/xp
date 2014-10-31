package com.enonic.wem.core.security;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.User;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;

class UserNodeTranslator
    extends PrincipalNodeTranslator
{
    public static final String EMAIL_KEY = "email";

    public static final String LOGIN_KEY = "login";

    private static final NodePath USER_PARENT = NodePath.newNodePath( NodePath.ROOT, "User" ).build();

    public static CreateNodeParams toCreateNodeParams( final User user )
    {
        Preconditions.checkNotNull( user );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( PrincipalKeyNodeTranslator.toNodeName( user.getKey() ).toString() ).
            parent( USER_PARENT );
        populateId( builder, user );

        final RootDataSet rootDataSet = new RootDataSet();
        addPrincipalPropertiesToDataSet( rootDataSet, user );
        rootDataSet.setProperty( EMAIL_KEY, Value.newString( user.getEmail() ) );
        rootDataSet.setProperty( LOGIN_KEY, Value.newString( user.getLogin() ) );
        builder.data( rootDataSet );

        return builder.build();
    }

    public static User fromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final RootDataSet rootDataSet = node.data();

        final User.Builder builder = User.create().
            email( getStringValue( rootDataSet, EMAIL_KEY ) ).
            login( getStringValue( rootDataSet, LOGIN_KEY ) );
        populatePrincipalProperties( builder, node );

        return builder.build();
    }


}
