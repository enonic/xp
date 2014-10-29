package com.enonic.wem.core.security;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeName;


class PrincipalKeyNodeTranslator
{

    public static NodeName toNodeName( final PrincipalKey principalKey )
    {
        return NodeName.from( principalKey.getId() );
    }

    public static PrincipalKey toKey( final Node node )
    {
        final String principalId = node.name().toString();

        final RootDataSet rootDataSet = node.data();

        final String principalType = getStringAndAssertNotNull( rootDataSet, PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY );
        final String userStoreKey = getStringAndAssertNotNull( rootDataSet, PrincipalNodeTranslator.USERSTORE_KEY );

        return PrincipalKey.from( new UserStoreKey( userStoreKey ), PrincipalType.valueOf( principalType ), principalId );
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

}
