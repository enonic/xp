package com.enonic.wem.api.node;

import com.enonic.wem.api.workspace.Workspace;

public class NodeVersionDiffQuery
{
    private final static int DEFAULT_SIZE = 10;

    private final Workspace source;

    private final Workspace target;

    private final int size;

    private final int from;

    private NodeVersionDiffQuery( Builder builder )
    {
        source = builder.source;
        target = builder.target;
        size = builder.size;
        from = builder.from;
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }


    public int getSize()
    {
        return size;
    }

    public int getFrom()
    {
        return from;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Workspace source;

        private Workspace target;

        private int size = DEFAULT_SIZE;

        private int from = 0;

        private Builder()
        {
        }

        public Builder source( final Workspace source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}
