package com.enonic.xp.node;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class GetNodesByParentParams
{
    private final NodeId parentId;

    private GetNodesByParentParams( Builder builder )
    {
        parentId = builder.parentId;
    }

    public NodeId getParentId()
    {
        return parentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId parentId;

        private Builder()
        {
        }

        public Builder parentId( NodeId parentId )
        {
            this.parentId = parentId;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.parentId, "ParentId must be given" );
        }

        public GetNodesByParentParams build()
        {
            this.validate();
            return new GetNodesByParentParams( this );
        }
    }
}
