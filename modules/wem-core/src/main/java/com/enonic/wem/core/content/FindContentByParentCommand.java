package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.Nodes;

final class FindContentByParentCommand
    extends AbstractContentCommand
{
    private final FindContentByParentParams params;

    private FindContentByParentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final FindContentByParentParams params )
    {
        return new Builder( params );
    }

    FindContentByParentResult execute()
    {
        final NodePath parentPath;

        boolean useWorkspaceOrdering = false;

        if ( params.getParentPath() == null )
        {
            parentPath = ContentNodeHelper.CONTENT_ROOT_NODE.asAbsolute();
            useWorkspaceOrdering = params.getChildOrder().isEmpty();
        }
        else
        {
            parentPath = ContentNodeHelper.translateContentPathToNodePath( params.getParentPath() );
        }

        final FindNodesByParentResult result = nodeService.findByParent( FindNodesByParentParams.create().
            parentPath( parentPath ).
            from( params.getFrom() ).
            size( params.getSize() ).
            childOrder( useWorkspaceOrdering ? ContextAccessor.current().getWorkspace().getChildOrder() : params.getChildOrder() ).
            build() );

        final Nodes nodes = result.getNodes();

        Contents contents = this.translator.fromNodes( nodes );

        return FindContentByParentResult.create().
            contents( contents ).
            totalHits( result.getTotalHits() ).
            hits( result.getHits() ).
            build();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final FindContentByParentParams params;

        public Builder( final FindContentByParentParams params )
        {
            this.params = params;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public FindContentByParentCommand build()
        {
            validate();
            return new FindContentByParentCommand( this );
        }
    }

}
