package com.enonic.wem.core.security;

import java.util.LinkedHashSet;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;


class PrincipalKeyNodeTranslator
{

    public static NodeName toNodeName( final PrincipalKey principalKey )
    {
        return NodeName.from( principalKey.getId() );
    }

    public static NodeId toNodeId( final PrincipalKey principalKey )
    {
        return NodeId.from( principalKey.toString() );
    }

    public static PrincipalKey toKey( final Node node )
    {
        final String principalId = node.name().toString();

        final RootDataSet rootDataSet = node.data();

        final String principalType = getStringAndAssertNotNull( rootDataSet, PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY );
        final String userStoreKey = getString( rootDataSet, PrincipalNodeTranslator.USER_STORE_KEY );

        final PrincipalType type = PrincipalType.valueOf( principalType );
        switch ( type )
        {
            case USER:
                return PrincipalKey.ofUser( new UserStoreKey( userStoreKey ), principalId );
            case GROUP:
                return PrincipalKey.ofGroup( new UserStoreKey( userStoreKey ), principalId );
            case ROLE:
                return PrincipalKey.ofRole( principalId );
        }
        throw new IllegalArgumentException( "Invalid principal type in node: " + type );
    }

    static PrincipalKeys fromNodes( final Nodes nodes )
    {
        final LinkedHashSet<PrincipalKey> principals = Sets.newLinkedHashSet();
        for ( final Node node : nodes )
        {
            principals.add( toKey( node ) );
        }
        return PrincipalKeys.from( principals );
    }

    private static String getStringAndAssertNotNull( final RootDataSet data, String key )
    {
        final Property property = data.getProperty( key );

        if ( property == null )
        {
            throw new IllegalArgumentException( "Failed to deserialize node to principal, missing property " + key );
        }

        return property.getValue().asString();
    }

    private static String getString( final RootDataSet data, String key )
    {
        final Property property = data.getProperty( key );
        return property == null ? null : property.getValue().asString();
    }

}
