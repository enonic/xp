package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.node.NodeIds;
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
    private final ContentIds contentIds;

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
        final ContentIds contentsWithInnerDependenciesIds =
            ContentIds.from( contentIds.stream().filter( excludeChildrenIds::contains ).collect( Collectors.toList() ) );

        if ( contentsWithInnerDependenciesIds.getSize() == 0 )
        {
            return NodeIds.empty();
        }

        return this.nodeService.findInternalDependencies( ContentNodeHelper.toNodeIds( contentsWithInnerDependenciesIds ) ).getIds();

    }

    private NodeIds findChildrenNodeIds()
    {
        final ContentIds contentsWithChildrenIds =
            ContentIds.from( contentIds.stream().filter( id -> !excludeChildrenIds.contains( id ) ).collect( Collectors.toList() ) );

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
        private ContentIds contentIds;

        private ContentIds excludeChildrenIds;

        public Builder contentIds( final ContentIds contentIds )
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
