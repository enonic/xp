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

public class GetOutgoingReferencesCommand
    extends AbstractNodeCommand
{
    private NodeId nodeId;

    private GetOutgoingReferencesCommand( Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
    }

    public NodeIds execute()
    {
        final ReturnValues returnValues = this.storageService.getIndexedData( nodeId, ReturnFields.from( NodeIndexPath.REFERENCE ),
                                                                              InternalContext.from( ContextAccessor.current() ) );

        final ReturnValue returnValue = returnValues.get( NodeIndexPath.REFERENCE.getPath() );

        if ( returnValue == null || returnValue.getValues().isEmpty() )
        {
            return NodeIds.empty();
        }

        final NodeIds.Builder builder = NodeIds.create();

        for ( final Object value : returnValue.getValues() )
        {
            builder.add( NodeId.from( value.toString() ) );
        }

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder( AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        protected void validate()
        {
            Preconditions.checkNotNull( this.nodeId, "NodeId must be set" );
            super.validate();
        }

        public GetOutgoingReferencesCommand build()
        {
            return new GetOutgoingReferencesCommand( this );
        }
    }
}
