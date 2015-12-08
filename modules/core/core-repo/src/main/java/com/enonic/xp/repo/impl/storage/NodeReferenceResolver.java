package com.enonic.xp.repo.impl.storage;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;

public class NodeReferenceResolver
{

    public static NodeIds getReferences( final Node node )
    {
        final NodeIds.Builder nodeIds = NodeIds.create();

        final PropertyTree data = node.data();

        final ImmutableList<Property> references = data.getProperties( ValueTypes.REFERENCE );

        for ( final Property property : references )
        {
            nodeIds.add( property.getReference().getNodeId() );
        }

        return nodeIds.build();
    }

}
