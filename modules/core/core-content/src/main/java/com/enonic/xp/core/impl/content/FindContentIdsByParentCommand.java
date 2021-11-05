package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.filter.Filters;

final class FindContentIdsByParentCommand
    extends AbstractContentCommand
{
    private final FindContentByParentParams params;

    private FindContentIdsByParentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final FindContentByParentParams params )
    {
        return new Builder( params );
    }

    FindContentIdsByParentResult execute()
    {
        final FindNodesByParentResult result = nodeService.findByParent( createFindNodesByParentParams() );

        final ContentIds contentIds = ContentIds.from( result.getNodeIds().getAsStrings() );

        return FindContentIdsByParentResult.create().
            contentIds( contentIds ).
            totalHits( result.getTotalHits() ).
            hits( result.getHits() ).
            build();
    }

    private FindNodesByParentParams createFindNodesByParentParams()
    {
        final FindNodesByParentParams.Builder findNodesParam = FindNodesByParentParams.create();

        setNodePathOrIdAsIdentifier( findNodesParam );

        findNodesParam.
            queryFilters( createFilters() ).
            from( params.getFrom() ).
            size( params.getSize() ).
            childOrder( params.getChildOrder() ).
            recursive( params.isRecursive() ).
            build();

        return findNodesParam.build();
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
            final NodeId parentId = NodeId.from( params.getParentId().toString() );
            findNodesParam.parentId( parentId );
        }
    }

    @Override
    protected Filters createFilters()
    {
        final Filters.Builder filters = Filters.create();
        super.createFilters().
            forEach( filters::add );
        params.getQueryFilters().
            forEach( filters::add );

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
            Preconditions.checkNotNull( params );
        }

        public FindContentIdsByParentCommand build()
        {
            validate();
            return new FindContentIdsByParentCommand( this );
        }
    }

}
