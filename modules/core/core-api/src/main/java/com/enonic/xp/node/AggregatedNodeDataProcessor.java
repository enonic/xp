package com.enonic.xp.node;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;

public final class AggregatedNodeDataProcessor
    implements NodeDataProcessor
{
    private final List<NodeDataProcessor> processors;

    public AggregatedNodeDataProcessor( final List<NodeDataProcessor> processors )
    {
        Preconditions.checkNotNull( processors, "processors must not be null" );
        this.processors = processors;
    }

    @Override
    public PropertyTree process( final PropertyTree originalData )
    {
        processors.forEach( processor -> processor.process( originalData ) );
        return originalData;
    }

    @Override
    public PropertyTree process( PropertyTree originalData, NodePath nodePath )
    {
        processors.forEach( processor -> processor.process( originalData, nodePath ) );
        return originalData;
    }
}
