package com.enonic.xp.lib.node;


public class NodeExistsHandler
    extends AbstractNodeHandler
{
    private final NodeKey key;

    public NodeExistsHandler( final Builder builder )
    {
        super( builder );
        this.key = builder.key;
    }

    @Override
    public Object execute()
    {
        return this.key.isId() ? nodeService.nodeExists( this.key.getAsNodeId() ) : nodeService.nodeExists( this.key.getAsPath() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeHandler.Builder<Builder>
    {
        private NodeKey key;

        private Builder()
        {
        }

        public Builder key( final NodeKey key )
        {
            this.key = key;
            return this;
        }

        public NodeExistsHandler build()
        {
            return new NodeExistsHandler( this );
        }
    }
}
