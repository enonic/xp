package com.enonic.wem.core.entity;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.query.expr.OrderExpr;

public class CreateNodeParams
{
    private NodePath parent;

    private String name;

    private RootDataSet data;

    private Attachments attachments;

    private IndexConfigDocument indexConfigDocument;

    private ImmutableSet<OrderExpr> orderExpressions;

    private boolean embed;

    private CreateNodeParams( Builder builder )
    {
        parent = builder.parent;
        name = builder.name;
        data = builder.data;
        attachments = builder.attachments;
        indexConfigDocument = builder.indexConfigDocument;
        embed = builder.embed;
        this.orderExpressions = ImmutableSet.copyOf( builder.orderExpressions );

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

    public ImmutableSet<OrderExpr> getOrderExpressions()
    {
        return orderExpressions;
    }

    public static final class Builder
    {
        private NodePath parent;

        private String name;

        private RootDataSet data;

        private Attachments attachments;

        private IndexConfigDocument indexConfigDocument;

        private Set<OrderExpr> orderExpressions = Sets.newLinkedHashSet();

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

        public Builder addOrderExpression( final OrderExpr orderExpression )
        {
            this.orderExpressions.add( orderExpression );
            return this;
        }

        public Builder addOrderExpression( final Set<OrderExpr> orderExpressions )
        {
            this.orderExpressions.addAll( orderExpressions );
            return this;
        }


        public CreateNodeParams build()
        {
            return new CreateNodeParams( this );
        }
    }
}
