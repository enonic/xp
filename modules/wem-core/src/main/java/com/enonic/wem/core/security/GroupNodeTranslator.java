package com.enonic.wem.core.security;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;

class GroupNodeTranslator
    extends PrincipalNodeTranslator
{
    private static final NodePath GROUP_PARENT = NodePath.newNodePath( NodePath.ROOT, "Group" ).build();

    public static CreateNodeParams toCreateNodeParams( final Group group )
    {
        Preconditions.checkNotNull( group );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( PrincipalKeyNodeTranslator.toNodeName( group.getKey() ).toString() ).
            parent( GROUP_PARENT );

        final RootDataSet rootDataSet = new RootDataSet();
        addPrincipalPropertiesToDataSet( rootDataSet, group );
        builder.data( rootDataSet );

        return builder.build();
    }

    public static Group fromNode( final Node node )
    {
        Preconditions.checkNotNull( node );

        final Group.Builder builder = Group.create();
        populatePrincipalProperties( builder, node );

        return builder.build();
    }

}
