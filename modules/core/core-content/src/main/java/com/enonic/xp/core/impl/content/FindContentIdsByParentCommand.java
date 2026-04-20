package com.enonic.xp.core.impl.content;

import java.util.Locale;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.Filters;

import static java.util.Objects.requireNonNull;

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

        final ContentIds contentIds = ContentNodeHelper.toContentIds( result.getNodeIds() );

        return FindContentIdsByParentResult.create().contentIds( contentIds ).totalHits( result.getTotalHits() ).build();
    }

    private FindNodesByParentParams createFindNodesByParentParams()
    {
        final Content parentContent = getParentContent();

        final FindNodesByParentParams.Builder builder = FindNodesByParentParams.create()
            .parentPath( ContentNodeHelper.translateContentPathToNodePath( parentContent.getPath() ) );

        ChildOrder childOrder = params.getChildOrder();
        if ( childOrder == null )
        {
            childOrder = parentContent.getChildOrder();
        }
        childOrder = childOrderWithLanguage( childOrder, parentContent.getLanguage() );

        return builder.queryFilters( Filters.create().addAll( createFilters() ).addAll( params.getQueryFilters() ).build() )
            .from( params.getFrom() )
            .size( params.getSize() )
            .childOrder( childOrder )
            .recursive( params.isRecursive() )
            .build();
    }

    private Content getParentContent()
    {
        if ( params.getParentPath() != null )
        {
            return getContent( params.getParentPath() );
        }
        else if ( params.getParentId() != null )
        {
            return getContent( params.getParentId() );
        }
        else
        {
            return getContent( ContentPath.ROOT );
        }
    }

    private static ChildOrder childOrderWithLanguage( final ChildOrder childOrder, final Locale language )
    {
        if ( childOrder == null || language == null )
        {
            return childOrder;
        }
        final ChildOrder.Builder builder = ChildOrder.create();
        for ( final OrderExpr orderExpr : childOrder.getOrderExpressions() )
        {
            if ( orderExpr instanceof FieldOrderExpr fieldOrderExpr && fieldOrderExpr.getLanguage() == null &&
                ContentIndexPath.DISPLAY_NAME.equals( fieldOrderExpr.getField().getIndexPath() ) )
            {
                builder.add( FieldOrderExpr.create( fieldOrderExpr.getField().getIndexPath(), fieldOrderExpr.getDirection(), language ) );
            }
            else
            {
                builder.add( orderExpr );
            }
        }
        return builder.build();
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
            requireNonNull( params, "params cannot be null" );
        }

        public FindContentIdsByParentCommand build()
        {
            validate();
            return new FindContentIdsByParentCommand( this );
        }
    }

}
