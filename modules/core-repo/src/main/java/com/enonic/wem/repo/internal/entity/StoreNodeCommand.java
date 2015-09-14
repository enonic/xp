package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;

public class StoreNodeCommand
    extends AbstractNodeCommand
{
    private final Node node;

    private final boolean updateMetadataOnly;

    private final StorageService storageService;

    private StoreNodeCommand( final Builder builder )
    {
        super( builder );
        this.node = builder.node;
        this.updateMetadataOnly = builder.updateMetadataOnly;
        this.storageService = builder.storageService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        if ( updateMetadataOnly )
        {
            return this.storageService.updateMetadata( this.node, InternalContext.from( context ) );
        }

        return this.storageService.store( this.node, InternalContext.from( context ) );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Node node;

        private boolean updateMetadataOnly = false;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder updateMetadataOnly( final boolean updateMetadataOnly )
        {
            this.updateMetadataOnly = updateMetadataOnly;
            return this;
        }

        public Builder storageService( final StorageService storageService )
        {
            this.storageService = storageService;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.node );
        }

        public StoreNodeCommand build()
        {
            this.validate();
            return new StoreNodeCommand( this );
        }
    }


}
