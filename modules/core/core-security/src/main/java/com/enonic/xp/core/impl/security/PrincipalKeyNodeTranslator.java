package com.enonic.xp.core.impl.security;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalType;


class PrincipalKeyNodeTranslator
{
    public static NodeName toNodeName( final PrincipalKey principalKey )
    {
        return NodeName.from( principalKey.getId() );
    }

    public static PrincipalKey toKey( final Node node )
    {
        final String principalId = node.name().toString();

        final PropertyTree rootDataSet = node.data();

        final String principalType = rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY );
        final String idProviderKey = rootDataSet.getString( PrincipalPropertyNames.ID_PROVIDER_KEY );

        final PrincipalType type = PrincipalType.valueOf( principalType );
        switch ( type )
        {
            case USER:
                return PrincipalKey.ofUser( IdProviderKey.from( idProviderKey ), principalId );
            case GROUP:
                return PrincipalKey.ofGroup( IdProviderKey.from( idProviderKey ), principalId );
            case ROLE:
                return PrincipalKey.ofRole( principalId );
        }
        throw new IllegalArgumentException( "Invalid principal type in node: " + type );
    }

    static PrincipalKeys fromNodes( final Nodes nodes )
    {
        return nodes.stream().map( PrincipalKeyNodeTranslator::toKey ).collect( PrincipalKeys.collector() );
    }
}
