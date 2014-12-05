package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;

public class NodeImportResult
{
    public final NodePaths importedNodes;

    private NodeImportResult( Builder builder )
    {
        importedNodes = builder.importedNodes.build();
    }

    public NodePaths getImportedNodes()
    {
        return importedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePaths.Builder importedNodes = NodePaths.create();

        private Builder()
        {
        }

        public Builder add( NodePath nodePath )
        {
            this.importedNodes.addNodePath( nodePath );
            return this;
        }

        public NodeImportResult build()
        {
            return new NodeImportResult( this );
        }
    }
}
