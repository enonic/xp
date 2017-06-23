package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetBinaryByVersionCommand
    extends AbstractGetBinaryCommand
{
    private final NodeVersionId nodeVersionId;

    private GetBinaryByVersionCommand( final Builder builder )
    {
        super( builder );
        this.nodeVersionId = builder.nodeVersionId;
    }

    public ByteSource execute()
    {
        final Node node = this.nodeStorageService.get( nodeVersionId, InternalContext.from( ContextAccessor.current() ) );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Cannot get binary reference, node with versionId: " + this.nodeVersionId + " not found" );
        }

        if ( binaryReference != null )
        {
            return getByBinaryReference( node );
        }
        else
        {
            return getByPropertyPath( node );
        }
    }


    public static GetBinaryByVersionCommand.Builder create()
    {
        return new GetBinaryByVersionCommand.Builder();
    }

    public static class Builder
        extends AbstractGetBinaryCommand.Builder<Builder>
    {
        private NodeVersionId nodeVersionId;


        public Builder()
        {
            super();
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeVersionId, "nodeVersionId not set" );
        }

        public GetBinaryByVersionCommand build()
        {
            this.validate();
            return new GetBinaryByVersionCommand( this );
        }
    }
}
