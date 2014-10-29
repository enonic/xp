package com.enonic.wem.core.security;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;

class RoleNodeTranslator
    extends PrincipalNodeTranslator
{
    private static final NodePath ROLE_PARENT = NodePath.newNodePath( NodePath.ROOT, "Group" ).build();

    public static CreateNodeParams toCreateNodeParams( final Role role )
    {
        Preconditions.checkNotNull( role );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( PrincipalKeyNodeTranslator.toNodeName( role.getKey() ).toString() ).
            parent( ROLE_PARENT );

        final RootDataSet rootDataSet = new RootDataSet();
        addPrincipalPropertiesToDataSet( rootDataSet, role );
        builder.data( rootDataSet );

        return builder.build();
    }

    public static Role fromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final Role.Builder builder = Role.create();
        populatePrincipalProperties( builder, node );

        return builder.build();
    }

}
