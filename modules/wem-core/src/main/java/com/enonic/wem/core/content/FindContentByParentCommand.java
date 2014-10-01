package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.Nodes;

final class FindContentByParentCommand
    extends AbstractFindContentCommand
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
        final NodePath nodePath;

        if ( params.getParentPath() == null )
        {
            nodePath = ContentNodeHelper.CONTENT_ROOT_NODE.asAbsolute();
        }
        else
        {
            nodePath = ContentNodeHelper.translateContentPathToNodePath( params.getParentPath() );
        }

        final FindNodesByParentResult result = nodeService.findByParent( FindNodesByParentParams.create().
            parentPath( nodePath ).
            from( params.getFrom() ).
            size( params.getSize() ).
            sorting( params.getSorting() ).
            build(), this.context );

        final Nodes nodes = result.getNodes();

        Contents contents = this.translator.fromNodes( nodes );

        return FindContentByParentResult.create().
            contents( contents ).
            totalHits( result.getTotalHits() ).
            hits( result.getHits() ).
            build();
    }

    public static class Builder
        extends AbstractFindContentCommand.Builder<Builder>
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
