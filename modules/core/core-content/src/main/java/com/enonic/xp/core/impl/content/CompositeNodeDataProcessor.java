package com.enonic.xp.core.impl.content;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodePath;

final class CompositeNodeDataProcessor
    implements NodeDataProcessor
{
    private final List<NodeDataProcessor> processors;

    CompositeNodeDataProcessor( final List<NodeDataProcessor> processors )
    {
        Preconditions.checkNotNull( processors, "processors must not be null" );
        this.processors = processors;
    }

    @Override
    public PropertyTree process( PropertyTree originalData, NodePath nodePath )
    {
        processors.forEach( processor -> processor.process( originalData, nodePath ) );
        return originalData;
    }
}
