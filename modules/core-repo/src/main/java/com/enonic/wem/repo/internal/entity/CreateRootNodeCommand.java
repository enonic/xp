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

    public RootNode execute()
    {
        final RootNode rootNode = RootNode.create().
            permissions( params.getPermissions() ).
            childOrder( params.getChildOrder() ).
            build();

        StoreNodeCommand.create().
            node( rootNode ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            execute();

        return rootNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateRootNodeParams params;

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
