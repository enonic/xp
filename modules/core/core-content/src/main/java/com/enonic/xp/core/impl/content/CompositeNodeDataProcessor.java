package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodePath;

final class CompositeNodeDataProcessor
    implements NodeDataProcessor
{
    private final List<NodeDataProcessor> processors;

    CompositeNodeDataProcessor( final List<NodeDataProcessor> processors )
    {
        Objects.requireNonNull( processors, "processors cannot be null" );
        this.processors = processors;
    }

    @Override
    public PropertyTree process( PropertyTree originalData, NodePath nodePath )
    {
        PropertyTree transformedData = originalData;
        for (NodeDataProcessor processor : processors) {
            transformedData = processor.process(transformedData, nodePath);
        }
        return transformedData;
    }
}
