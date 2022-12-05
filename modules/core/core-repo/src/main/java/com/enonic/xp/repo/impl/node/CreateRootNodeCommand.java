package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.InternalContext;

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

    public Node execute()
    {
        final Node rootNode = Node.createRoot().
            permissions( params.getPermissions() ).
            childOrder( params.getChildOrder() ).
            build();

        return this.nodeStorageService.store( rootNode, InternalContext.from( ContextAccessor.current() ) );
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
