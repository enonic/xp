package com.enonic.wem.core.entity;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.index.IndexConfigDocument;

public class CreateNodeParams
{
    private NodePath parent;

    private String name;

    private RootDataSet data;

    private Attachments attachments;

    private IndexConfigDocument indexConfigDocument;

    private ChildOrder childOrder;

    private boolean embed;

    private CreateNodeParams( Builder builder )
    {
        parent = builder.parent;
        name = builder.name;
        data = builder.data;
        attachments = builder.attachments;
        indexConfigDocument = builder.indexConfigDocument;
        embed = builder.embed;
        this.childOrder = builder.childOrder;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public RootDataSet getData()
    {
        return data;
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public static final class Builder
    {
        private NodePath parent;

        private String name;

        private RootDataSet data;

        private Attachments attachments;

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private boolean embed;

        private Builder()
        {
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder data( final RootDataSet data )
        {
            this.data = data;
            return this;
        }

        public Builder attachments( final Attachments attachments )
        {
            this.attachments = attachments;
            return this;
        }

        public Builder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder embed( boolean embed )
        {
            this.embed = embed;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public CreateNodeParams build()
        {
            return new CreateNodeParams( this );
        }
    }
}
