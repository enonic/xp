package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.filter.Filters;

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
        final FindNodesByParentResult result = nodeService.findByParent( createFindNodesByParentParams() );

        final Nodes nodes = this.nodeService.getByIds( result.getNodeIds() );

        final Contents contents = this.translator.fromNodes( nodes, true );

        return FindContentByParentResult.create().contents( contents ).totalHits( result.getTotalHits() ).hits( result.getHits() ).build();
    }

    private FindNodesByParentParams createFindNodesByParentParams()
    {
        final FindNodesByParentParams.Builder findNodesParam = FindNodesByParentParams.create();

        setNodePathOrIdAsIdentifier( findNodesParam );

        return findNodesParam.queryFilters( createFilters() )
            .from( params.getFrom() )
            .size( params.getSize() )
            .childOrder( params.getChildOrder() )
            .build();
    }

    private void setNodePathOrIdAsIdentifier( final FindNodesByParentParams.Builder findNodesParam )
    {
        if ( params.getParentPath() == null && params.getParentId() == null )
        {
            final NodePath parentPath = ContentNodeHelper.getContentRoot();
            findNodesParam.parentPath( parentPath );
        }
        else if ( params.getParentPath() != null )
        {
            final NodePath parentPath = ContentNodeHelper.translateContentPathToNodePath( params.getParentPath() );
            findNodesParam.parentPath( parentPath );
        }
        else
        {
            final NodeId parentId = NodeId.from( params.getParentId() );
            findNodesParam.parentId( parentId );
        }
    }

    @Override
    protected Filters createFilters()
    {
        final Filters.Builder filters = Filters.create();
        super.createFilters().forEach( filters::add );
        params.getQueryFilters().forEach( filters::add );

        return filters.build();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final FindContentByParentParams params;

        Builder( final FindContentByParentParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public FindContentByParentCommand build()
        {
            validate();
            return new FindContentByParentCommand( this );
        }
    }

}
