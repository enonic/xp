package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;

public class NodeExportResult
{
    private final NodePaths exportedNodes;

    private NodeExportResult( Builder builder )
    {
        exportedNodes = builder.exportedNodes.build();
    }

    public NodePaths getExportedNodes()
    {
        return exportedNodes;
    }

    public int size()
    {
        return exportedNodes.getSize();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private final NodePaths.Builder exportedNodes = NodePaths.create();

        private Builder()
        {
        }

        public Builder add( final NodePath nodePath )
        {
            this.exportedNodes.addNodePath( nodePath );
            return this;
        }

        public NodeExportResult build()
        {
            return new NodeExportResult( this );
        }
    }
}
