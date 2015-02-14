package com.enonic.xp.security.impl;

import java.util.LinkedHashSet;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.PropertyTree;
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

        final PropertyTree rootDataSet = node.data();

        final String principalType = rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY );
        final String userStoreKey = rootDataSet.getString( PrincipalPropertyNames.USER_STORE_KEY );

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
}
