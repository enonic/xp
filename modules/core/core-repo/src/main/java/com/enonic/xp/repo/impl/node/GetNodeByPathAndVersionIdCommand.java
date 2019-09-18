package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetNodeByPathAndVersionIdCommand
    extends AbstractNodeCommand
{

    private final NodePath nodePath;

    private final NodeVersionId versionId;

    private GetNodeByPathAndVersionIdCommand( final Builder builder )
    {
        super( builder );

        this.nodePath = builder.nodePath;
        this.versionId = builder.versionId;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        return this.nodeStorageService.getNode( nodePath, versionId, InternalContext.from( context ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {

        private NodePath nodePath;

        private NodeVersionId versionId;

        private Builder()
        {
            super();
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder versionId( NodeVersionId versionId )
        {
            this.versionId = versionId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.nodePath );
            Preconditions.checkNotNull( this.versionId );
        }

        public GetNodeByPathAndVersionIdCommand build()
        {
            this.validate();
            return new GetNodeByPathAndVersionIdCommand( this );
        }

    }

}
