package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.expr.QueryExpr;

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

    /**
     * The deprecated findIdsByParent expressed as the query it always was, so there is one implementation of searching by parent rather
     * than two that can drift apart. Reading the parent up front is what keeps this answering with ContentNotFoundException for one that
     * does not exist, where a query answers with nothing.
     */
    FindContentIdsByParentResult execute()
    {
        final Content parentContent = getParentContent();

        final ContentQuery.Builder query = ContentQuery.create()
            .parentPath( parentContent.getPath() )
            .recursive( params.isRecursive() )
            .from( params.getFrom() )
            .size( params.getSize() );

        params.getQueryFilters().forEach( query::queryFilter );

        final ChildOrder childOrder = ContentChildOrder.withLanguage(
            params.getChildOrder() != null ? params.getChildOrder() : parentContent.getChildOrder(), parentContent.getLanguage() );

        if ( childOrder != null && !childOrder.isEmpty() )
        {
            query.queryExpr( QueryExpr.from( null, childOrder.getOrderExpressions() ) );
        }

        final FindContentIdsByQueryResult result = FindContentIdsByQueryCommand.create()
            .query( query.build() )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        return FindContentIdsByParentResult.create().contentIds( result.getContentIds() ).totalHits( result.getTotalHits() ).build();
    }

    private Content getParentContent()
    {
        final ContentPath parentPath = params.getParentPath();
        if ( parentPath != null && !parentPath.isRoot() )
        {
            return getContent( parentPath );
        }
        else if ( params.getParentId() != null )
        {
            return getContent( params.getParentId() );
        }
        else
        {
            return GetContentByPathCommand.create( ContentPath.ROOT, this ).allowRoot().build().execute();
        }
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
