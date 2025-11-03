package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.NodeDataProcessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

public class DuplicateNodeHandler
    extends AbstractNodeHandler
{
    private final NodeId nodeId;

    private final String name;

    private final NodePath parent;

    private final boolean includeChildren;

    private final NodeDataProcessor dataProcessor;

    private final RefreshMode refresh;

    private DuplicateNodeHandler( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.name = builder.name;
        this.parent = builder.parent;
        this.includeChildren = builder.includeChildren;
        this.dataProcessor = builder.dataProcessor;
        this.refresh = builder.refresh;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Object execute()
    {
        return new NodeMapper( nodeService.duplicate( DuplicateNodeParams.create()
                                                          .nodeId( this.nodeId )
                                                          .name( this.name )
                                                          .parent( this.parent )
                                                          .includeChildren( this.includeChildren )
                                                          .dataProcessor( this.dataProcessor )
                                                          .refresh( this.refresh )
                                                          .build() ).getNode() );
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeId nodeId;

        private String name;

        private NodePath parent;

        private boolean includeChildren = true;

        private NodeDataProcessor dataProcessor;

        private RefreshMode refresh;

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public Builder dataProcessor( final NodeDataProcessor dataProcessor )
        {
            this.dataProcessor = dataProcessor;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public DuplicateNodeHandler build()
        {
            return new DuplicateNodeHandler( this );
        }
    }
}
