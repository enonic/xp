package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;

abstract class AbstractProjectReadAccessCommand
    extends AbstractProjectRolesCommand
{
    final Context projectRepoContext;

    final NodeService nodeService;

    AbstractProjectReadAccessCommand( final Builder builder )
    {
        super( builder );
        this.nodeService = builder.nodeService;

        this.projectRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( projectName.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();
    }

    public static class Builder<B extends Builder>
        extends AbstractProjectRolesCommand.Builder<B>
    {
        private NodeService nodeService;

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeService, "nodeService cannot be null" );
        }
    }

}
