package com.enonic.xp.core.impl.content;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

public class ResolveDuplicateDependenciesCommand
    extends AbstractContentCommand
{
    private final Map<ContentId, ContentPath> contentIds;

    private final ContentIds excludeChildrenIds;

    private final ContentIds.Builder resultBuilder;

    private ResolveDuplicateDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludeChildrenIds = builder.excludeChildrenIds;
        this.resultBuilder = ContentIds.create();
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        final NodeIds dependentNodeIds = findDependentNodeIds();
        final NodeIds childrenNodeIds = findChildrenNodeIds();

        return resultBuilder.
            addAll( ContentNodeHelper.toContentIds( dependentNodeIds ) ).
            addAll( ContentNodeHelper.toContentIds( childrenNodeIds ) ).build();
    }

    private NodeIds findDependentNodeIds()
    {
        final Map<NodeId, NodePath> nodesWithDependencies = Maps.newHashMap();

        contentIds.entrySet().forEach( entry -> {
            nodesWithDependencies.put( NodeId.from( entry.getKey().toString() ), entry.getValue() != null
                ? ContentNodeHelper.translateContentPathToNodePath( entry.getValue() )
                : null );
        } );
        if ( nodesWithDependencies.keySet().size() == 0 )
        {
            return NodeIds.empty();
        }

        return this.nodeService.findInternalDependencies( nodesWithDependencies ).getIds();

    }

    private NodeIds findChildrenNodeIds()
    {
        final ContentIds contentsWithChildrenIds = ContentIds.from(
            contentIds.keySet().stream().filter( id -> !excludeChildrenIds.contains( id ) ).collect( Collectors.toList() ) );

        if ( contentsWithChildrenIds.getSize() == 0 )
        {
            return NodeIds.empty();
        }
        final Contents contentsWithChildren = GetContentByIdsCommand.
            create( new GetContentByIdsParams( contentsWithChildrenIds ) ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            build().
            execute();

        final ContentQuery query =
            ContentQuery.create().queryExpr( constructExprToFindChildren( contentsWithChildren ) ).size( -1 ).build();

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( query ).build();

        return this.nodeService.findByQuery( nodeQuery ).getNodeIds();
    }

    private QueryExpr constructExprToFindChildren( final Contents contents )
    {
        final ContentPaths contentsPaths = contents.getPaths();

        final FieldExpr fieldExpr = FieldExpr.from( "_path" );

        ConstraintExpr expr = null;

        for ( ContentPath contentPath : contentsPaths )
        {
            ConstraintExpr likeExpr = CompareExpr.like( fieldExpr, ValueExpr.string( "/content/" + contentPath.asRelative() + "/*" ) );
            expr = expr != null ? LogicalExpr.or( expr, likeExpr ) : likeExpr;
        }

        expr = LogicalExpr.and( expr, CompareExpr.notIn( fieldExpr, contentsPaths.stream().
            map( contentPath -> ValueExpr.string( "/content/" + contentPath.asRelative() ) ).collect( Collectors.toList() ) ) );

        return QueryExpr.from( expr, new FieldOrderExpr( fieldExpr, OrderExpr.Direction.ASC ) );

    }


    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private Map<ContentId, ContentPath> contentIds;

        private ContentIds excludeChildrenIds;

        public Builder contentIds( final Map<ContentId, ContentPath> contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveDuplicateDependenciesCommand build()
        {
            validate();
            return new ResolveDuplicateDependenciesCommand( this );
        }

    }
}
