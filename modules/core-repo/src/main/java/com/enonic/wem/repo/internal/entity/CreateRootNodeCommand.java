package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.RootNode;

public class CreateRootNodeCommand
    extends AbstractNodeCommand
{
    private final CreateRootNodeParams params;


    private CreateRootNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public RootNode execute()
    {
        final RootNode rootNode = RootNode.create().
            permissions( params.getPermissions() ).
            childOrder( params.getChildOrder() ).
            build();

        StoreNodeCommand.create().
            node( rootNode ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexServiceInternal ).
            build().
            execute();

        return rootNode;
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateRootNodeParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( final CreateRootNodeParams params )
        {
            this.params = params;
            return this;
        }

        public CreateRootNodeCommand build()
        {
            return new CreateRootNodeCommand( this );
        }

    }

}
