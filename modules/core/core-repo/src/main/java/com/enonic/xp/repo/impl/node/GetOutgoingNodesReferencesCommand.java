package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.ReturnValues;

public class GetOutgoingNodesReferencesCommand
    extends AbstractNodeCommand
{
    private NodeIds nodeIds;

    private GetOutgoingNodesReferencesCommand( Builder builder )
    {
        super( builder );
        nodeIds = builder.nodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeIds execute()
    {

        final NodeIds.Builder builder = NodeIds.create();

        // TODO: Make bulk-version

        for ( final NodeId nodeId : this.nodeIds )
        {
            final ReturnValues returnValues = this.storageService.getIndexedData( nodeId, ReturnFields.from( NodeIndexPath.REFERENCE ),
                                                                                  InternalContext.from( ContextAccessor.current() ) );

            final ReturnValue returnValue = returnValues.get( NodeIndexPath.REFERENCE.getPath() );

            if ( returnValue == null || returnValue.getValues().isEmpty() )
            {
                continue;
            }

            for ( final Object value : returnValue.getValues() )
            {
                builder.add( NodeId.from( value.toString() ) );
            }
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        private Builder()
        {
        }

        public Builder( AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeId( NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( this.nodeIds, "NodeIds must be set" );
            super.validate();
        }

        public GetOutgoingNodesReferencesCommand build()
        {
            return new GetOutgoingNodesReferencesCommand( this );
        }
    }
}
