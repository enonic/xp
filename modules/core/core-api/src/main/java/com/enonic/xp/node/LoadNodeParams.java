package com.enonic.xp.node;

public class LoadNodeParams
{
    private final Node node;

    private LoadNodeParams( final Builder builder )
    {
        node = builder.node;
    }

    public Node getNode()
    {
        return node;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private Builder()
        {
        }

        public Builder node( final Node val )
        {
            node = val;
            return this;
        }

        public LoadNodeParams build()
        {
            return new LoadNodeParams( this );
        }
    }
}
