package com.enonic.xp.core.impl.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodePath;

final class CompositeNodeDataProcessor
    implements NodeDataProcessor
{
    private final List<NodeDataProcessor> processors;

    CompositeNodeDataProcessor( final List<NodeDataProcessor> processors )
    {
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

    static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<NodeDataProcessor> processors = new ArrayList<>();

        public Builder add( final NodeDataProcessor processor )
        {
            this.processors.add( processor );
            return this;
        }

        public CompositeNodeDataProcessor build()
        {
            return new CompositeNodeDataProcessor( processors );
        }
    }
}
