package com.enonic.wem.core.content;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FieldSort;
import com.enonic.wem.api.content.GetContentByParentParams;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.query.QueryResult;

final class GetContentByParentCommand
    extends AbstractFindContentCommand
{
    private final boolean populateChildIds;

    private final GetContentByParentParams params;

    private GetContentByParentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.populateChildIds = builder.populateChildIds;
    }

    public static Builder create( final GetContentByParentParams params )
    {
        return new Builder( params );
    }

    Contents execute()
    {
        final NodeQuery query = createByPathQuery();

        // TODO: Fix, should return NodeQueryResult
        final QueryResult queryResult = this.queryService.find( query, this.context.getWorkspace() );

        final ContentQueryResult contentQueryResult = translateToContentQueryResult( queryResult );

        final Set<ContentId> contentIds = contentQueryResult.getContentIds();

        final Nodes nodes = nodeService.getByIds( getAsEntityIds( ContentIds.from( contentIds ) ), this.context );

        final Contents contents = this.translator.fromNodes( nodes );

        if ( populateChildIds && contents.isNotEmpty() )
        {
            return ChildContentIdsResolver.create().
                context( this.context ).
                nodeService( this.nodeService ).
                blobService( this.blobService ).
                contentTypeService( this.contentTypeService ).
                translator( this.translator ).
                queryService( this.queryService ).
                build().
                resolve( contents );
        }
        else
        {
            return contents;
        }
    }

    private NodeQuery createByPathQuery()
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

        final Set<OrderExpr> orderBys = Sets.newHashSet();

        for ( final FieldSort fieldSort : this.params.getSorting() )
        {
            final FieldOrderExpr orderByExpr = new FieldOrderExpr( new FieldExpr( fieldSort.getFieldName() ),
                                                                   OrderExpr.Direction.valueOf( fieldSort.getDirection().name() ) );
            orderBys.add( orderByExpr );
        }

        return NodeQuery.newNodeQuery().
            addQueryFilter( Filter.newValueQueryFilter().
                fieldName( NodeIndexDocumentFactory.PARENT_PATH_KEY ).
                add( Value.newString( nodePath.toString() ) ).
                build() ).
            query( new QueryExpr( orderBys ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            build();
    }

    public static class Builder
        extends AbstractFindContentCommand.Builder<Builder>
    {
        private GetContentByParentParams params;

        private boolean populateChildIds = true;

        public Builder( final GetContentByParentParams params )
        {
            this.params = params;
        }

        public Builder populateChildIds( final boolean populateChildIds )
        {
            this.populateChildIds = populateChildIds;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public GetContentByParentCommand build()
        {
            validate();
            return new GetContentByParentCommand( this );
        }
    }

}
