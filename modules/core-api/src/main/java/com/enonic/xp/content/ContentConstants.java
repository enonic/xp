package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

@Beta
public class ContentConstants
{
    public static final String CONTENT_DEFAULT_ANALYZER = "content_default";

    public static final Branch BRANCH_DRAFT = Branch.create().
        name( "draft" ).
        build();

    public static final Branch BRANCH_MASTER = Branch.create().
        name( "master" ).
        build();

    public static final Repository CONTENT_REPO = Repository.create().
        id( RepositoryId.from( "cms-repo" ) ).
        build();

    public static final Context CONTEXT_DRAFT = ContextBuilder.create().
        branch( BRANCH_DRAFT ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final Context CONTEXT_MASTER = ContextBuilder.create().
        branch( BRANCH_MASTER ).
        repositoryId( CONTENT_REPO.getId() ).
        build();

    public static final String CONTENT_ROOT_NAME = "content";

    public static final NodePath CONTENT_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath CONTENT_ROOT_PATH = NodePath.newNodePath( CONTENT_ROOT_PARENT, CONTENT_ROOT_NAME ).build();

    public static final NodeType CONTENT_NODE_COLLECTION = NodeType.from( "content" );

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();

}
